package com.example.idkcontroll.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.idkcontroll.R

/**
 * A simple [Fragment] subclass.
 * Use the [OutputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OutputFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_output, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            OutputFragment()
    }
}