package com.example.idkcontroll

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
        var controllers: MutableList<InputDevice> = mutableListOf()
        var driver1id: Int? = null
        var driver2id: Int? = null
        var connectSuccessful = true
        val mainFr = MainFragment()
        val configFr = ConfigFragment()
        val pairingFr = PairingFragment()
        const val bTSCANRQ = 2
        @RequiresApi(Build.VERSION_CODES.O_MR1)
        fun findControllers()
        {
            controllers.clear()
            for (deviceId: Int in InputDevice.getDeviceIds())
            {
                val device = InputDevice.getDevice(deviceId)
                val sources = device.sources
                if (device.isEnabled && sources and InputDevice.SOURCE_CLASS_JOYSTICK == InputDevice.SOURCE_CLASS_JOYSTICK && sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD)
                {
                    controllers
                        .takeIf { !it.contains(device) }
                        ?.add(device)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private val perms: Array<String> = arrayOf(Manifest.permission.BLUETOOTH_SCAN)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        btManager = getSystemService(BluetoothManager::class.java)
        btAdapter = btManager.adapter

        if (macadress != intent.getStringExtra(ConnectActivity.macadressExtraName))
        {
            macadress = intent.getStringExtra(ConnectActivity.macadressExtraName)
            btSocket = null
            isConnected = false
        }

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener {
            var res = true
            when (it.itemId) {
                R.id.ic_main -> {
//                    if (driver1id == null || driver2id == null) {
                    if (driver1id == null) {
                        AlertDialog.Builder(this).apply {
                            setMessage("You have to select game controllers first")
                            setPositiveButton("Ok") { _, _ -> run {} }
                        }.create().show()
                        res = false
                    } else
                        changeFragment(mainFr)
                }
                R.id.ic_config -> changeFragment(configFr)
            }
            res
        }

        /*
        if (checkSelfPermission(perms[0]) != PackageManager.PERMISSION_GRANTED)
        {
            AlertDialog.Builder(this).apply {
                setTitle("No scan perm")
                setMessage("Please allow the permission and let us connect to your device")
                setPositiveButton("Ok :)") {_, _ ->
                    run {
                        ActivityCompat.requestPermissions(this@ControlActivity, perms, bTSCANRQ)
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
        */
        ActivityCompat.requestPermissions(this@ControlActivity, perms, bTSCANRQ)
        pairDevices(1)
    }

    private fun pairDevices(maxAttempts: Int) {
        GlobalScope.launch {
            pairDevicesInner(maxAttempts)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun pairDevicesInner(maxAttempts: Int) {
        var maxAttemptsCopy = maxAttempts
        if (btSocket == null)
            isConnected = false
        if (isConnected)
            return
        this@ControlActivity.runOnUiThread {
            changeFragment(pairingFr)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            )
        }

        while (--maxAttemptsCopy >= 0 && (btSocket == null || !isConnected)) {
            try {
                val device: BluetoothDevice = btAdapter.getRemoteDevice(macadress)
                btSocket = device.createRfcommSocketToServiceRecord(myUUID)
                btAdapter.cancelDiscovery()
                btSocket!!.connect()
                connectSuccessful = true
            } catch (e: IOException) {
                connectSuccessful = false
                e.printStackTrace()
            }
            if (connectSuccessful) {
                this@ControlActivity.runOnUiThread {
                    changeFragment(configFr)
                }
                Log.i("bluetooth Pairing", "Succeeded")
                isConnected = true
                return
            }
            else {
                Log.w("bluetooth Pairing", "Failed")
                this@ControlActivity.runOnUiThread {
                    Toast.makeText(this, "Failed to pair", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (maxAttemptsCopy < 0) {
            this@ControlActivity.runOnUiThread {
                Toast.makeText(this, "Time out", Toast.LENGTH_SHORT)
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                this@ControlActivity.finish()
            }
            return
        }
        this@ControlActivity.runOnUiThread {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun sendCommand(command: String) {
        if (btSocket != null)
        {
            try {
                btSocket!!.outputStream.write(command.toByteArray())
            } catch (e: IOException) {
                Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Disconnected, repairing...", Toast.LENGTH_SHORT).show()
            isConnected = false
            pairDevices(3)
        }
    }

    private fun sendCommand(command: Int) {
        if (btSocket != null)
        {
            try {
                btSocket!!.outputStream.write(command)
            } catch (e: IOException) {
                Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Disconnected, repairing...", Toast.LENGTH_SHORT).show()
            isConnected = false
            pairDevices(3)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == bTSCANRQ)
        {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("SCAN permission", "Allowed")
                pairDevices(1)
            } else
            {
                Log.e("SCAN permission", "Declined")
                finish()
            }
        }
    }

    override fun dispatchGenericMotionEvent(ev: MotionEvent?): Boolean {
        when (ev?.device?.id)
        {
            driver1id -> {
                mainFr.inputFragment.log("LT: " + ev?.getAxisValue(MotionEvent.AXIS_LTRIGGER))
                mainFr.inputFragment.log("RT: " + ev?.getAxisValue(MotionEvent.AXIS_RTRIGGER))
                mainFr.inputFragment.log((ev?.actionButton.toString() + ": " + ev?.pressure))
                mainFr.inputFragment.submit()
                return true
            }
            driver2id -> {

            }
        }
        return super.dispatchGenericMotionEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.repeatCount != null && event.repeatCount > 1)
            return super.dispatchKeyEvent(event)
        when (event?.device?.id)
        {
            driver1id -> {
                if (event?.keyCode == 62)
                {
                    if (event.action == 0) {
                        sendCommand(62)
                    } else
                    {
                        sendCommand(61)
                    }
                } else if (event?.keyCode == 102)
                {
                    sendCommand(500)
                    sendCommand(250)
                }

                mainFr.inputFragment.log("Keycode: " + event?.keyCode.toString())
                mainFr.inputFragment.log("Repeat count: " + event?.repeatCount.toString())
                mainFr.inputFragment.log("Action: " + event?.action.toString())
                mainFr.inputFragment.submit()
            }
            driver2id -> {

            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun changeFragment(fragment: Fragment?)
    {
        if (fragment == null)
            return
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}