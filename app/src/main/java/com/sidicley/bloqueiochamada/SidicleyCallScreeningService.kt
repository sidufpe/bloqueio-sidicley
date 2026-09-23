
package com.sidicley.bloqueiochamada

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallScreeningService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SidicleyCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val prefs = getSharedPreferences("sidicley_prefs", MODE_PRIVATE)
        val ativo = prefs.getBoolean("ativo", true)
        if(!ativo){
            respondToCall(callDetails, CallResponse.Builder().build()); return
        }
        val modo = prefs.getString("modo","nao_contatos") ?: "nao_contatos"
        val numeroRaw = callDetails.handle?.schemeSpecificPart ?: ""
        val numero = numeroRaw.replace(Regex("[^0-9]"),"").takeLast(11)

        if(numero.isEmpty()){
            // número privado
            bloquear(callDetails, numeroRaw, "Privado/Desconhecido")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.get(applicationContext)
            val isBlack = db.blacklistDao().exists(numero)
            val isWhite = db.whitelistDao().exists(numero)
            val isInContacts = isInContacts(numero)

            val deveBloquear = when(modo){
                "lista_negra" -> isBlack
                "lista_branca" -> !isWhite && !isInContacts
                else -> { // nao_contatos
                    if(isWhite) false
                    else if(isBlack) true
                    else !isInContacts
                }
            }

            if(deveBloquear){
                db.blockedDao().insert(BlockedCall(number = numero, displayName = numeroRaw, reason = "Modo:$modo / Contato:$isInContacts"))
                bloquear(callDetails, numeroRaw, modo)
            } else {
                respondToCall(callDetails, CallResponse.Builder().setDisallowCall(false).setRejectCall(false).setSkipCallLog(false).setSkipNotification(false).build())
            }
        }
    }

    private fun bloquear(details: Call.Details, raw:String, motivo:String){
        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()
        respondToCall(details, response)
    }

    private fun isInContacts(number: String): Boolean {
        if(number.length<8) return false
        val resolver: ContentResolver = contentResolver
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        var cursor: Cursor? = null
        return try{
            cursor = resolver.query(uri, arrayOf(ContactsContract.PhoneLookup._ID), null, null, null)
            cursor != null && cursor.count > 0
        } finally { cursor?.close() }
    }
}
