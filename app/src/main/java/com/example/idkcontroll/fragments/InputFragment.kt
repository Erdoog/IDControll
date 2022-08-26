package com.example.idkcontroll.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.idkcontroll.R

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class InputFragment : Fragment() {
    private var message: String = ""
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
        return inflater.inflate(R.layout.fragment_input, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.submitBtn).setOnClickListener {
            val command: String = view.findViewById<EditText>(R.id.inputEd).text.toString()
            if (command != "")
            {
//                (activity as? ControlActivity)!!.sendCommand(command + "\n")
//                (activity as? ControlActivity)!!.sendCommand(command)
                view.findViewById<EditText>(R.id.inputEd).text.clear()
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}