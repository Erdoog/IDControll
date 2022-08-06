package com.example.idkcontroll

import android.os.Build
import android.view.InputDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class AdapterControllers(
    private val refresher: IRefresher,
    private val dataset: List<InputDevice>
) : RecyclerView.Adapter<AdapterControllers.ItemViewHolder>(){
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_controller, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with (holder.itemView)
        {
            findViewById<TextView>(R.id.controllerNameTv).text = dataset[position].name
            findViewById<TextView>(R.id.controllerIdTv).text = dataset[position].id.toString()
            if (dataset[position].id == ControlActivity.driver1id) {
                findViewById<ImageView>(R.id.controllerInd).setImageResource(R.drawable.shape_first_circle)
            }
            else if (dataset[position].id == ControlActivity.driver2id) {
                findViewById<ImageView>(R.id.controllerInd).setImageResource(R.drawable.shape_second_circle)
            }
            else {
                findViewById<ImageView>(R.id.controllerInd).setImageResource(R.drawable.shape_accent_circle)
            }
            findViewById<Button>(R.id.selectFirstBtn).setOnClickListener {
                if (dataset[position].id == ControlActivity.driver2id)
                    ControlActivity.driver2id = null
                ControlActivity.driver1id = dataset[position].id
                refresher.refresh()
            }
            findViewById<Button>(R.id.selectSecondBtn).setOnClickListener {
                if (dataset[position].id == ControlActivity.driver1id)
                    ControlActivity.driver1id = null
                ControlActivity.driver2id = dataset[position].id
                refresher.refresh()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}