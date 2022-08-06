package com.example.idkcontroll.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.idkcontroll.AdapterControllers
import com.example.idkcontroll.ControlActivity
import com.example.idkcontroll.IRefresher
import com.example.idkcontroll.R

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class ConfigFragment: Fragment(), IRefresher {
    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun refresh() {
        ControlActivity.findControllers()
        view?.findViewById<RecyclerView>(R.id.controllersRC)?.adapter =
            AdapterControllers(this, ControlActivity.controllers.toList())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_config, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ControlActivity.findControllers()
        with (view.findViewById<RecyclerView>(R.id.controllersRC))
        {
            adapter = AdapterControllers(this@ConfigFragment, ControlActivity.controllers.toList())
            layoutManager = LinearLayoutManager(context)
        }
        view.findViewById<Button>(R.id.refreshCONTRBtn).setOnClickListener {
            ControlActivity.findControllers()
            view.findViewById<RecyclerView>(R.id.controllersRC).adapter =
                AdapterControllers(this@ConfigFragment, ControlActivity.controllers.toList())
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ConfigFragment.
         */
        @JvmStatic
        fun newInstance() =
            ConfigFragment()
    }
}