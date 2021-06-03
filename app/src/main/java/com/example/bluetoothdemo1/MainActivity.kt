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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var BA:BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var pairedDevices:Set<BluetoothDevice>

        if(BA==null){
            val BlutoothNotFoundError = "Bluetooth not supported"
            Toast.makeText(this, BlutoothNotFoundError, Toast.LENGTH_SHORT).show()
            Log.d(TAG, BlutoothNotFoundError)
        }

        if(BA.isEnabled){
            val BluetoothEnabled = "Bluetooth enabled"
            Log.d(TAG, BluetoothEnabled)
        }

        val progressBar:ProgressBar = findViewById(R.id.progress_bar)

        val button: Button = findViewById<Button>(R.id.paired_list_button)
        button.setOnClickListener(View.OnClickListener {

            progressBar.visibility = View.VISIBLE

            var intentOn: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intentOn,0)
            Log.d(TAG, "turned on")

            var getVisible:Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
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

            //TODO: put list in adapter for recyclerView
            val rv:RecyclerView = findViewById(R.id.recyclerView)
            rv.layoutManager = LinearLayoutManager(this)
            rv.adapter = ItemAdapter(list)

            Log.d(TAG, " rv adapter item count: ${rv.childCount}")

            progressBar.visibility = View.GONE
            val headerTextView = findViewById<TextView>(R.id.header_textview)
            headerTextView.text = "Paired devices"
        })



        val detectButton:Button = findViewById(R.id.detect_button)
        detectButton.setOnClickListener{

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


    }
}