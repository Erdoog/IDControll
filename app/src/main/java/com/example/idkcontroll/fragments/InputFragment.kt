package com.example.idkcontroll.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.idkcontroll.R

/**
 * A simple [Fragment] subclass.
 * Use the [InputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputFragment : Fragment() {
    var message: String = ""
    fun log(message: String) {
        this.message += message + '\n'
    }
    fun submit() {
        view?.findViewById<TextView>(R.id.inputTv)?.text = message
        message = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            InputFragment()
    }
}