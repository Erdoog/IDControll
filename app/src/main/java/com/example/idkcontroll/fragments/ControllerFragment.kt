package com.example.idkcontroll.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.idkcontroll.R

/**
 * A simple [Fragment] subclass.
 * Use the [ControllerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ControllerFragment : Fragment() {
    private var isMain: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isMain = it.getBoolean("isMain")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_controller, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment ControllerFragment.
         */
        @JvmStatic
        fun newInstance(param1: Boolean) =
            ControllerFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isMain", param1)
                }
            }
    }
}