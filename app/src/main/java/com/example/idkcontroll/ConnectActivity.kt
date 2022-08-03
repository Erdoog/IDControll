package com.example.idkcontroll

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.textclassifier.ConversationActions
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.idkcontroll.databinding.ActivityConnectBinding
import com.example.idkcontroll.databinding.ActivityLearnBinding

class ConnectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConnectBinding
    private lateinit var btManager: BluetoothManager
    private var btAdapter: BluetoothAdapter? = null
    private lateinit var devices: Set<BluetoothDevice>

    fun getDevices(): Set<BluetoothDevice> {
        return devices
    }

    private val perms = arrayOf(
        Manifest.permission.BLUETOOTH
    )

    private val REQUESTCODE_BT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConnectBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        var counter = 0

        if (!checkAllPerms()) {
            requestBt()
        }

        btManager = getSystemService(BluetoothManager::class.java)
        btAdapter = btManager.adapter

        if (btAdapter == null) {
            Toast.makeText(this, "No bluetooth", Toast.LENGTH_LONG).show()
            return
        }
        if (!checkAllPerms()) {
            Log.w("Second Check: ", "Start")
            requestBt()
            Log.w("Second Check: ", "End")
        } else {
            turnOnBluetooth()
        }
    }

    private fun checkAllPerms(): Boolean {
        for (perm in perms) {
            if (ActivityCompat.checkSelfPermission(this, perm)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w( "checkAllPerms: ", perm)
                return false
            }
        }
        Log.i("checkAllPerms: ", "Everything passed")
        return true
    }


    @SuppressLint("MissingPermission")
    private fun turnOnBluetooth() {
        if (btAdapter?.isEnabled == false) {
            startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    @SuppressLint("MissingPermission")
    fun pairedDeviceList() {
        devices = btAdapter!!.bondedDevices

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun requestBt(): Unit {
        ActivityCompat.requestPermissions(this, perms, REQUESTCODE_BT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUESTCODE_BT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("requestPerm", "Set perms")
                turnOnBluetooth()
                with(binding.refreshBtn)
                {
                    text = getString(R.string.refresh)
                    setOnClickListener {
                    }
                }
            } else {
                Log.i("requestPerm", "Declined perms")
                with(binding.refreshBtn)
                {
                    text = "Provide permission"
                    setOnClickListener {
                        requestBt()
                    }
                }
            }
        }
    }
}