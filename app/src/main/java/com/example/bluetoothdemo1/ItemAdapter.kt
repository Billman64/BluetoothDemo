package com.example.bluetoothdemo1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val list:ArrayList<String>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    val TAG = this.javaClass.simpleName + "/asdf"

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nameTv: TextView = itemView.findViewById(R.id.name_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_layout,parent,false)
        return ItemViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = list[position]
        holder.nameTv.text = currentItem
    }

    override fun getItemCount(): Int {
        return list.size
    }


}