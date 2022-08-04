package com.example.idkcontroll.connectrecycler

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.idkcontroll.ConnectActivity
import com.example.idkcontroll.R

class ItemAdapter(
    private val context: Context,
    private val dataset: List<BluetoothDevice>,
    private var onItemClick: (Int) -> Unit

) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(
        val view: View,
    ) : RecyclerView.ViewHolder(view) {
        private val deviceName: TextView = view.findViewById(R.id.itemName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_connect, parent, false)
        val aViewHolder = ItemViewHolder(adapterLayout)
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
                    this@ItemAdapter.onItemClick(pos)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}