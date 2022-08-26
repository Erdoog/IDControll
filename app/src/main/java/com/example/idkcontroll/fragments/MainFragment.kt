package com.example.idkcontroll.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.idkcontroll.R

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    val inputFragment = InputFragment()
    private val outputFragment = OutputFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val transaction = childFragmentManager.beginTransaction()

        transaction.replace(R.id.fragmentContainerInput, inputFragment)
        transaction.replace(R.id.fragmentContainerOutput, outputFragment)

        transaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment()
    }
}