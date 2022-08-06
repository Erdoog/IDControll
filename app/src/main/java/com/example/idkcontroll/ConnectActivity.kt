package com.example.idkcontroll

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.system.exitProcess

class ConnectActivity : AppCompatActivity() {
    companion object {
        const val macadressExtraName = "com.example.idkcontroll.macadressPkg"
    }
    private lateinit var btManager: BluetoothManager
    private var btAdapter: BluetoothAdapter? = null
    private lateinit var devices: Set<BluetoothDevice>
    private lateinit var rcAdapter: AdapterBT
    private val onItemClick: (Int) -> Unit = {pos ->
        run {
            val connectIntent = Intent(this, ControlActivity::class.java)
            connectIntent.putExtra(macadressExtraName, devices.toList()[pos].address)
            startActivity(connectIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private val perms = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT
    )

    private val bTRQ = 1

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        devices = setOf()
        btManager = getSystemService(BluetoothManager::class.java)
        btAdapter = btManager.adapter

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        supportActionBar?.hide()

        rcAdapter = AdapterBT(devices.toList(), onItemClick)
        with(findViewById<RecyclerView>(R.id.deviceListRv))
        {
            adapter = rcAdapter
            layoutManager = LinearLayoutManager(context)
        }

        if (btAdapter == null) {
            Log.e("BluetoothAdapter", "Bluetooth is not supported")
            Toast.makeText(this, "No bluetooth", Toast.LENGTH_LONG).show()
            return
        }

        findViewById<Button>(R.id.refreshBTBtn).setOnClickListener {
            pairedDeviceList()
        }

        if (checkSelfPermission(perms[0]) != PackageManager.PERMISSION_GRANTED) {
            Log.i("firstCheck", "request invoked-------------------------------------------------------------------------------")
            requestBt()
            return
        }

        if (turnOnBluetooth(false))
        {
            return
        }
        Log.i("onCreate: ", "Got to pairedDeviceList()")
        pairedDeviceList()
        Log.i("onCreate: ", "Passed pairedDeviceList()")
        rcAdapter = AdapterBT(devices.toList(), onItemClick)
        findViewById<RecyclerView>(R.id.deviceListRv).adapter = rcAdapter
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun turnOnBluetooth(req: Boolean): Boolean {
        if (btAdapter?.isEnabled == false) {

            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_GRANTED)
                startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            else if (req) {
                Log.i("turnOnBluetooth: ", "perms requested")
                requestBt()
            }
            return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun pairedDeviceList() {
        if (requestBt()) {
            Log.e("deviceList", "No perm")
            return
        }
        if (!btAdapter!!.isEnabled) {
            turnOnBluetooth(true)
            Log.e("pairedDeviceList: ", "stopped at enabled check")
            return
        }
        devices = btAdapter!!.bondedDevices
        rcAdapter = AdapterBT(devices.toList(), onItemClick)
        findViewById<RecyclerView>(R.id.deviceListRv).adapter = rcAdapter
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBt(): Boolean {
        if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_GRANTED)
        {
            Log.i("requestBt", "perm already acquired")
            return false
        }
        else if (shouldShowRequestPermissionRationale(perms[0]))
        {
            AlertDialog.Builder(this).apply {
                setMessage("Please allow us to use bluetooth or we gotta send Elnur over to you\nThe declination will crash the app")
                setTitle("Bluetooth restricted")
                setPositiveButton("Ok :)") { _, _ ->
                    requestPermissions(perms, bTRQ)
                }
                  setNegativeButton("No") { _, _ ->
                      exitProcess(1)
                  }
            }.create().show()
        }
        else
        {
//            ActivityCompat.requestPermissions(this@ConnectActivity, perms, REQUEST_CODE_BT)
            requestPermissions(perms, bTRQ)
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == bTRQ) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("requestPerm", "Set perms")
                with(findViewById<Button>(R.id.refreshBTBtn))
                {
                    text = getString(R.string.refresh)
                    setOnClickListener {
                        Log.i("refreshBtn", "Invoked updated delegate")
                        pairedDeviceList()
                    }
                }
            }
            else {
                Log.e("Request_Permission", "Declined")

//                exitProcess(1)
                with(findViewById<Button>(R.id.refreshBTBtn))
                {
                    text = getString(R.string.provicepermission)
                    setOnClickListener {
                        requestBt()
                    }
                }
            }
        }
    }
}