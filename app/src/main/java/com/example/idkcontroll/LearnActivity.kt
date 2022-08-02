package com.example.idkcontroll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.idkcontroll.databinding.ActivityLearnBinding

class LearnActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLearnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)
        binding = ActivityLearnBinding.inflate(layoutInflater)
    }
}