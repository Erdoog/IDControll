package com.example.idkcontroll

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.idkcontroll.fragments.ConfigFragment
import com.example.idkcontroll.fragments.MainFragment
import com.example.idkcontroll.fragments.PairingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class ControlActivity : AppCompatActivity() {

    companion object {
        var isConnected = false
//        val myUUID: UUID = UUID.randomUUID()
        val myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var macadress: String? = null
        var btSocket: BluetoothSocket? = null
        lateinit var btManager: BluetoothManager
        lateinit var btAdapter: BluetoothAdapter
        lateinit var progressBar: ProgressBar
    }

    private var connectSuccessful = true
    private val mainFr = MainFragment()
    private val configFr = ConfigFragment()
    private val pairingFr = PairingFragment()
    private val REQUEST_CODE_BTSCAN = 2

    private val perms: Array<String> = arrayOf(Manifest.permission.BLUETOOTH_SCAN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        macadress = intent.getStringExtra(ConnectActivity.macadressExtraName)

        changeFragment(pairingFr)

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener {
            when (it.itemId)
            {
                R.id.ic_main -> changeFragment(mainFr)
                R.id.ic_config -> changeFragment(configFr)
            }
            true
        }

        if (checkSelfPermission(perms[0]) != PackageManager.PERMISSION_GRANTED)
        {
            AlertDialog.Builder(this).apply {
                setTitle("No scan perm")
                setMessage("Please allow the permission and let us connect to your device")
                setPositiveButton("Ok :)") {_, _ ->
                    run {
                        ActivityCompat.requestPermissions(this@ControlActivity, perms, REQUEST_CODE_BTSCAN)
                    }
                }
                setNegativeButton("No") {_, _ ->
                    run {
                        finish()
                    }
                }
            }.create().show()
            return
        }
        pairDevices()
    }

    private fun pairDevices()
    {
        btManager = getSystemService(BluetoothManager::class.java)
        btAdapter = btManager.adapter
        GlobalScope.launch {
            asyncPairDevices()
        }.invokeOnCompletion {
            this@ControlActivity.runOnUiThread(java.lang.Runnable {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            })
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        )
    }

    private fun switchProgressbar(mode: Boolean)
    {
        if (mode)
            progressBar.visibility = View.VISIBLE
        else
            progressBar.visibility = View.GONE
    }

    @SuppressLint("MissingPermission")
    suspend fun asyncPairDevices() {
        try {
            if (btSocket == null || !isConnected)
            {
                val device: BluetoothDevice = btAdapter.getRemoteDevice(macadress)
                btSocket = device.createRfcommSocketToServiceRecord(myUUID)
                btAdapter.cancelDiscovery()
                btSocket!!.connect()
                connectSuccessful = true
            }
        } catch (e: IOException)
        {
            connectSuccessful = false
            e.printStackTrace()
        }
        if (!connectSuccessful) {
            finish()
            Log.w("BT Pairing", "Failed")
            this@ControlActivity.runOnUiThread(java.lang.Runnable {
            Toast.makeText(this, "Failed to pair", Toast.LENGTH_SHORT).show()
            })
        }
        else {
            this@ControlActivity.runOnUiThread(java.lang.Runnable {
                changeFragment(mainFr)
            })
            Log.i("BT Pairing", "Succeeded")
            isConnected = true
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

    override fun onStop() {
        super.onStop()
        btSocket = null
        isConnected = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BTSCAN)
        {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("SCAN permission", "Allowed")
                pairDevices()
            } else
            {
                Log.e("SCAN permission", "Declined")
                finish()
            }
        }
    }
}