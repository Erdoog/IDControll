package com.example.idkcontroll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BindAdapter(
    private val binds: MutableList<Bind>
) : RecyclerView.Adapter<BindAdapter.bindViewHolder>() {
    class bindViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bindViewHolder {
        TODO("Not yet implemented")
        return BindAdapter.bindViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_config,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: bindViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
        return binds.size
    }
}