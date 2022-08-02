package com.example.idkcontroll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.idkcontroll.databinding.ActivityLearnBinding
import com.example.idkcontroll.databinding.ActivityMainBinding
import com.example.idkcontroll.fragments.ConfigFragment
import com.example.idkcontroll.fragments.MainFragment

class MainActivity : AppCompatActivity() {
//    /*
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        startActivity(Intent(this, ConnectActivity::class.java))
    }
//     */

    /*
    private val mainFr = MainFragment()
    private val configFr = ConfigFragment()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeFragment(mainFr)

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId)
            {
                R.id.ic_main -> changeFragment(mainFr)
                R.id.ic_config -> changeFragment(configFr)
                else -> {}
            }

            true
        }

    }
    private fun changeFragment(fragment: Fragment?)
    {
        if (fragment == null)
            return
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
     */
}