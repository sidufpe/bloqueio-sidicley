
package com.sidicley.bloqueiochamada

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sidicley.bloqueiochamada.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: BlockedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        db = AppDatabase.get(this)

        adapter = BlockedAdapter { item ->
            lifecycleScope.launch { db.blockedDao().delete(item) }
        }
        b.rvBloqueadas.layoutManager = LinearLayoutManager(this)
        b.rvBloqueadas.adapter = adapter

        val prefs = getSharedPreferences("sidicley_prefs", MODE_PRIVATE)
        b.switchAtivo.isChecked = prefs.getBoolean("ativo", true)
        b.switchAtivo.setOnCheckedChangeListener { _, v -> prefs.edit().putBoolean("ativo", v).apply() }

        b.rgModo.check(when(prefs.getString("modo","nao_contatos")){
            "lista_branca" -> b.rbListaBranca.id
            "lista_negra" -> b.rbListaNegra.id
            else -> b.rbNaoContatos.id
        })
        b.rgModo.setOnCheckedChangeListener { _, id ->
            val modo = when(id){
                b.rbListaBranca.id -> "lista_branca"
                b.rbListaNegra.id -> "lista_negra"
                else -> "nao_contatos"
            }
            prefs.edit().putString("modo", modo).apply()
        }

        b.btnAddBranca.setOnClickListener {
            val num = b.etNumero.text.toString().trim()
            if(num.length<8){ toast("Número inválido"); return@setOnClickListener }
            lifecycleScope.launch { db.whitelistDao().insert(WhiteContact(number = normalize(num))); toast("Adicionado à lista branca") }
        }
        b.btnAddPreta.setOnClickListener {
            val num = b.etNumero.text.toString().trim()
            if(num.length<8){ toast("Número inválido"); return@setOnClickListener }
            lifecycleScope.launch { db.blacklistDao().insert(BlackContact(number = normalize(num))); toast("Adicionado à lista negra") }
        }

        b.btnPedirPermissoes.setOnClickListener { pedirPermissoes() }
        pedirPermissoes()

        lifecycleScope.launch {
            db.blockedDao().observeAll().collectLatest { adapter.submitList(it) }
        }
    }

    private fun pedirPermissoes(){
        val perms = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ANSWER_PHONE_CALLS)
        if(perms.any { ActivityCompat.checkSelfPermission(this,it)!=PackageManager.PERMISSION_GRANTED }){
            ActivityCompat.requestPermissions(this, perms, 100)
        }
        // Pedir papel de Call Screening (Android 10+)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val roleManager = getSystemService(RoleManager::class.java)
            if(roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) && !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)){
                startActivityForResult(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING), 101)
            }
        } else {
            val tm = getSystemService(TelecomManager::class.java)
            if(!tm.defaultDialerPackage.equals(packageName)){
                // Para versões antigas, tenta pedir para ser filtro
                try{
                    val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                    startActivity(intent)
                }catch(e:Exception){}
            }
        }
    }

    private fun normalize(n:String) = n.replace(Regex("[^0-9]"),"").takeLast(11)
    private fun toast(s:String) = Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
}
