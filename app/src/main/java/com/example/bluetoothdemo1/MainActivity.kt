package com.example.bluetoothdemo1

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val TAG = this.javaClass.simpleName + "/asdf"


    var BA:BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate()")

        initializeBluetooth()

        val listPairedDevicesButton: Button = findViewById(R.id.paired_list_button)
        listPairedDevicesButton.setOnClickListener{
            listPairedDevices()
        }

        val detectButton:Button = findViewById(R.id.detect_button)
        detectButton.setOnClickListener{
            detectDevices()
        }
    }


    private fun initializeBluetooth() {
        if(BA==null){
            val BlutoothNotFoundError = "Bluetooth not supported"
            Toast.makeText(this, BlutoothNotFoundError, Toast.LENGTH_SHORT).show()
            Log.d(TAG, BlutoothNotFoundError)
        }

        if(BA.isEnabled) Log.d(TAG, "Bluetooth enabled")
    }

    private fun detectDevices() {
        val headerTextView:TextView = findViewById(R.id.header_textview)
        val rv:RecyclerView = findViewById(R.id.recyclerView)
        val progressBar:ProgressBar = findViewById(R.id.progress_bar)

        headerTextView.text = ""
        rv.adapter = null
        progressBar.visibility = View.VISIBLE

//            private val bleDeviceListAdapter = BluetoothAdapter.getDefaultAdapter().
//            var deviceList:BleDeviceListAdapter()

        var callback:ScanCallback =
            object:ScanCallback(){
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    Log.d(TAG, "onScanResult() ${result}")

                }

                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                    super.onBatchScanResults(results)
                    Log.d(TAG, "onBatchScanResults() count: ${results?.count()}")
                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    Log.d(TAG, "onScanFailed() errorCode: ${errorCode}")
                }
            }

        CoroutineScope(Dispatchers.IO).launch{
            BA.bluetoothLeScanner.startScan(callback)
            delay(5000)

            withContext(Dispatchers.Main){
                BA.bluetoothLeScanner.stopScan(callback)
//                    var results = BA.
//                    Log.d(TAG, "scan result: ${results}")

                progressBar.visibility = View.GONE
            }
        }
    }

    private fun listPairedDevices() {
        val headerTextView:TextView = findViewById(R.id.header_textview)
        val rv:RecyclerView = findViewById(R.id.recyclerView)
        val progressBar:ProgressBar = findViewById(R.id.progress_bar)

        headerTextView.text = ""
        rv.adapter = null
        progressBar.visibility = View.VISIBLE

        var intentOn = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intentOn,0)
        Log.d(TAG, "turned on")

        var getVisible = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        val discoverable = "discoverable for 2 mins"
        Log.d(TAG, "visibility: ${getVisible.toString()} $discoverable")

        var pairedDevices = BA.bondedDevices
        var list:ArrayList<String> = arrayListOf()
        Log.d(TAG, "# of devices found: ${pairedDevices.count()}")
        for(bt:BluetoothDevice in pairedDevices){
            list.add(bt.name)
            Log.i(TAG, "device: ${bt.name}")
            Log.d(TAG, " class: ${bt.bluetoothClass} bondState: ${bt.bondState} type: ${bt.type} #uuids: ${bt.uuids.size}")
        }
        Log.d(TAG, "list size: ${list.size}")

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = ItemAdapter(list)

        progressBar.visibility = View.GONE
        headerTextView.text = "Paired devices"
    }
}