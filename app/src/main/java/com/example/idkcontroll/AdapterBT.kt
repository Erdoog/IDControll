package com.example.idkcontroll

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterBT(
    private val dataset: List<BluetoothDevice>,
    private var onItemClick: (Int) -> Unit

) : RecyclerView.Adapter<AdapterBT.ItemViewHolder>() {
    class ItemViewHolder(
        val view: View,
    ) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_connect, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder.view)
        {
            findViewById<TextView>(R.id.itemName).text = dataset[position].name
            findViewById<TextView>(R.id.itemMac).text = dataset[position].address
            setOnClickListener {
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION)
                    this@AdapterBT.onItemClick(pos)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}