package com.example.idkcontroll

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ConnectActivity : AppCompatActivity() {
    private lateinit var btAdapter: BluetoothAdapter
    private lateinit var devices: Set<BluetoothDevice>
    private val reqEnableBt: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
    }

    fun pairedDeviceList(): Unit
    {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}