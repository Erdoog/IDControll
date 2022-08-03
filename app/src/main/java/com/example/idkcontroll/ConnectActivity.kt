package com.example.idkcontroll

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.textclassifier.ConversationActions
import android.widget.Toast
import androidx.core.app.ActivityCompat

class ConnectActivity : AppCompatActivity() {
    private lateinit var btManager: BluetoothManager
    private var btAdapter: BluetoothAdapter? = null
    private lateinit var devices: Set<BluetoothDevice>
    private val reqEnableBt = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        btManager = getSystemService(BluetoothManager::class.java)
        btAdapter = btManager.adapter
        if (btAdapter == null)
        {
            Toast.makeText(this, "No bluetooth", Toast.LENGTH_LONG).show()
            throw Exception("You are poor")
        }

        if (!btAdapter!!.isEnabled)
        {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, "you gimme perm", Toast.LENGTH_LONG).show()
                return
            }
            startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    fun pairedDeviceList(): Unit
    {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}