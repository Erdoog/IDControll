package com.example.idkcontroll.connectrecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.idkcontroll.R

class ItemAdapter(
    private val context: Context,
    private var dataset: MutableList<device>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(
        private val view: View,
    ) : RecyclerView.ViewHolder(view)
    {
        private val deviceName: TextView = view.findViewById(R.id.itemName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        TODO("Not yet implemented")
        val adapterLayout: View = LayoutInflater.from(parent.context).inflate(R.layout.item_connect, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

}