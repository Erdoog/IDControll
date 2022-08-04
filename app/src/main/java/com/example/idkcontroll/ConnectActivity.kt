package com.example.idkcontroll

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.textclassifier.ConversationActions
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.idkcontroll.connectrecycler.ItemAdapter
import kotlin.system.exitProcess

class ConnectActivity : AppCompatActivity() {
    companion object {
        const val macadressExtraName = "com.example.idkcontroll.macadressPkg"
    }
    private lateinit var btManager: BluetoothManager
    private var btAdapter: BluetoothAdapter? = null
    private lateinit var devices: Set<BluetoothDevice>
    private lateinit var rcAdapter: ItemAdapter
    private val onItemClick: (Int) -> Unit = {pos ->
        run {
            var connectIntent = Intent(this, ControlActivity::class.java)
            connectIntent.putExtra(macadressExtraName, devices.toList()[pos].address)
            startActivity(connectIntent)
        }
    }

    private val perms = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT
    )

    private val REQUEST_CODE_BT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        devices = setOf()
        btManager = getSystemService(BluetoothManager::class.java)
        btAdapter = btManager.adapter

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        supportActionBar?.hide()

        rcAdapter = ItemAdapter(this, devices.toList(), onItemClick)
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

        findViewById<Button>(R.id.refreshBtn).setOnClickListener {
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
        rcAdapter = ItemAdapter(this, devices.toList(), onItemClick)
        with(findViewById<RecyclerView>(R.id.deviceListRv))
        {
            adapter = rcAdapter
        }
    }

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
        rcAdapter = ItemAdapter(this, devices.toList(), onItemClick)
        findViewById<RecyclerView>(R.id.deviceListRv).adapter = rcAdapter
    }

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
                    requestPermissions(perms, REQUEST_CODE_BT)
                }
                  setNegativeButton("No") { _, _ ->
                      exitProcess(1)
                  }
            }.create().show()
        }
        else
        {
//            ActivityCompat.requestPermissions(this@ConnectActivity, perms, REQUEST_CODE_BT)
            requestPermissions(perms, REQUEST_CODE_BT)
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("requestPerm", "Set perms")
                with(findViewById<Button>(R.id.refreshBtn))
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
                with(findViewById<Button>(R.id.refreshBtn))
                {
                    text = "Provide bt permission"
                    setOnClickListener {
                        requestBt()
                    }
                }
            }
        }
    }
}