
package com.sidicley.bloqueiochamada
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sidicley.bloqueiochamada.databinding.ItemBlockedBinding
import java.text.SimpleDateFormat
import java.util.*

class BlockedAdapter(val onDelete:(BlockedCall)->Unit): ListAdapter<BlockedCall, BlockedAdapter.VH>(DIFF){
 class VH(val b:ItemBlockedBinding): RecyclerView.ViewHolder(b.root)
 companion object{
  val DIFF = object: DiffUtil.ItemCallback<BlockedCall>(){ override fun areItemsTheSame(a:BlockedCall,b:BlockedCall)=a.id==b.id; override fun areContentsTheSame(a:BlockedCall,b:BlockedCall)=a==b }
 }
 override fun onCreateViewHolder(p:ViewGroup, t:Int)=VH(ItemBlockedBinding.inflate(LayoutInflater.from(p.context),p,false))
 override fun onBindViewHolder(h:VH, pos:Int){
  val item=getItem(pos)
  h.b.tvNumero.text=item.number
  h.b.tvMotivo.text=item.reason
  h.b.tvData.text=SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(item.timestamp))
  h.b.btnExcluir.setOnClickListener{ onDelete(item) }
 }
}
