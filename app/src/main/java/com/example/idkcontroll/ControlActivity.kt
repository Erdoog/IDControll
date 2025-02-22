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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.idkcontroll.fragments.ConfigFragment
import com.example.idkcontroll.fragments.MainFragment
import com.example.idkcontroll.fragments.PairingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*


class ControlActivity : AppCompatActivity() {
    companion object {
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
            btSocket?.close()
            btSocket = null
        }

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener {
            var res = true
            when (it.itemId) {
                R.id.ic_main -> {
//                    if (driver1id == null || driver2id == null) {
//                    if (driver1id == null) {
//                        AlertDialog.Builder(this).apply {
//                            setMessage("You have to select game controllers first")
//                            setPositiveButton("Ok") { _, _ -> run {} }
//                        }.create().show()
//                        res = false
//                    } else
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
        if (checkSelfPermission(perms[0]) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this@ControlActivity, perms, bTSCANRQ)
        pairDevices(1)
    }

    private fun pairDevices(maxAttempts: Int) {
        if (btSocket != null && btSocket!!.isConnected) {
            return
        }
        changeFragment(pairingFr)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        )
        GlobalScope.launch {
            pairDevicesInner(maxAttempts)
        }
    }

    @SuppressLint("MissingPermission")
    fun pairDevicesInner(maxAttempts: Int) {
        var maxAttemptsCopy = maxAttempts

        while (--maxAttemptsCopy >= 0 && (btSocket == null || !btSocket!!.isConnected)) {
            connectSuccessful = false
            try {
                val device: BluetoothDevice = btAdapter.getRemoteDevice(macadress)
                btSocket = device.createRfcommSocketToServiceRecord(myUUID)
                btAdapter.cancelDiscovery()
                btSocket!!.connect()
                connectSuccessful = true
            } catch (e: IOException) {
                connectSuccessful = false
                e.printStackTrace()
                Log.e("Bluetooth socket", "IO exception")
            } catch (e: NullPointerException)
            {
                e.printStackTrace()
                Log.e("Bluetooth socket", "null")
            }
            if (connectSuccessful) {
                this@ControlActivity.runOnUiThread {
                    changeFragment(configFr)
                }
                Log.i("bluetooth Pairing", "Succeeded")
            }
            else {
                Log.w("bluetooth Pairing", "Failed")
                this@ControlActivity.runOnUiThread {
                    Toast.makeText(this, "Failed to pair", Toast.LENGTH_SHORT).show()
                }
            }
        }
        this@ControlActivity.runOnUiThread {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        if (!btSocket!!.isConnected || btSocket == null) {
            this@ControlActivity.runOnUiThread {
                Toast.makeText(this, "Time out", Toast.LENGTH_SHORT).show()
                this@ControlActivity.finish()
            }
            return
        }
    }

    fun sendCommand(command: String) {
        if (btSocket != null && btSocket!!.isConnected)
        {
            try {
//                btSocket!!.outputStream.write((command + '\n').toByteArray())
                btSocket!!.outputStream.write(command.toByteArray())
                return
            } catch (e: IOException) {
                Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                return
            } catch (e: NullPointerException) {}
        }
        Toast.makeText(this, "Disconnected, repairing...", Toast.LENGTH_SHORT).show()
        pairDevices(3)
    }

    fun sendCommand(command: Int) {
        if (btSocket != null && !btSocket!!.isConnected)
        {
            try {
                btSocket!!.outputStream.write(command)
                Log.i("Sent command", command.toString())
            } catch (e: IOException) {
                Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Disconnected, repairing...", Toast.LENGTH_SHORT).show()
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

    fun <T> throttleFirst(
        skipMs: Long = 300L,
        coroutineScope: CoroutineScope,
        destinationFunction: (T) -> Unit
    ): (T) -> Unit {
        var throttleJob: Job? = null
        return { param: T ->
            if (throttleJob?.isCompleted != false) {
                throttleJob = coroutineScope.launch {
                    destinationFunction(param)
                    delay(skipMs)
                }
            }
        }
    }

    var l: ((String) -> Unit) = throttleFirst(100L, GlobalScope) { eve ->
        this@ControlActivity.runOnUiThread {
            sendCommand(eve)
            mainFr.inputFragment.log(eve[0] + eve[1].code.toString())
        }
    }

    var r: ((String) -> Unit) = throttleFirst(100L, GlobalScope) { eve ->
        this@ControlActivity.runOnUiThread {
            sendCommand(eve)
            mainFr.inputFragment.log(eve[0] + eve[1].code.toString())
            mainFr.inputFragment.submit()
        }
    }

    override fun dispatchGenericMotionEvent(ev: MotionEvent?): Boolean {
        if (ev == null)
            return super.dispatchGenericMotionEvent(ev)
        when (ev.device.id)
        {
            driver1id -> {
                if (ev.getAxisValue(MotionEvent.AXIS_RZ) == -1.0f || ev.getAxisValue(MotionEvent.AXIS_RZ) == 1.0f) {
                        sendCommand("" + 'R' + ((-ev.getAxisValue(MotionEvent.AXIS_RZ) + 1) * 63 + 1).toInt().toChar())
                        mainFr.inputFragment.log(
                            "" + 'R' + ((-ev.getAxisValue(MotionEvent.AXIS_RZ) + 1) * 63 + 1).toInt().toChar().code
                        )
                }
                else {
                    r("" + 'R' + ((-ev.getAxisValue(MotionEvent.AXIS_RZ) + 1) * 63 + 1).toInt().toChar())
                }
                if (ev.getAxisValue(MotionEvent.AXIS_Y) == -1.0f || ev.getAxisValue(MotionEvent.AXIS_Y) == 1.0f) {
                        sendCommand("" + 'L' + ((-ev.getAxisValue(MotionEvent.AXIS_Y) + 1) * 63 + 1).toInt().toChar())
                        mainFr.inputFragment.log(
                            "" + 'L' + ((-ev.getAxisValue(MotionEvent.AXIS_Y) + 1) * 63 + 1).toInt().toChar().code
                        )
                        mainFr.inputFragment.submit()
                }
                else {
                    l( "" + 'L' + ((-ev.getAxisValue(MotionEvent.AXIS_Y) + 1) * 63 + 1).toInt().toChar())
                }
                return true
            }
            driver2id -> {
//                sendCommand("2TL " + ev.getAxisValue(MotionEvent.AXIS_LTRIGGER).toString() + '\n')
//                sendCommand("2TR " + ev.getAxisValue(MotionEvent.AXIS_RTRIGGER).toString() + '\n')
//                sendCommand("2SX " + ev.getAxisValue(MotionEvent.AXIS_X).toString() + '\n')
//                sendCommand("2SY " + ev.getAxisValue(MotionEvent.AXIS_Y).toString() + '\n')
//                sendCommand("2CX " + ev.getAxisValue(MotionEvent.AXIS_Z).toString() + '\n')
//                sendCommand("2CY " + ev.getAxisValue(MotionEvent.AXIS_RZ).toString() + '\n')
//                sendCommand("2DX " + ev.getAxisValue(MotionEvent.AXIS_HAT_Y).toString() + '\n')
//                sendCommand("2DY " + ev.getAxisValue(MotionEvent.AXIS_HAT_X).toString() + '\n')
                return true
            }
        }
        return super.dispatchGenericMotionEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event == null || event.repeatCount > 1)
            return super.dispatchKeyEvent(event)
        when (event.device.id)
        {
            driver1id -> {
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