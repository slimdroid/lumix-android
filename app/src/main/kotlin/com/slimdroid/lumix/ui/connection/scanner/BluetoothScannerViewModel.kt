package com.slimdroid.lumix.ui.connection.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.slimdroid.lumix.R
import com.slimdroid.lumix.data.BleControlManager
import com.slimdroid.lumix.utils.SingleEventLiveData
import java.util.concurrent.TimeUnit

private val SCAN_PERIOD_IN_MILLIS = TimeUnit.SECONDS.toMillis(30)

class DeviceScannerViewModel(
    application: Application,
    private val bluetoothAdapter: BluetoothAdapter?
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val handler: Handler = Handler(Looper.getMainLooper())

    private val _deviceList = MutableLiveData<List<BluetoothDevice>>()
    val deviceList: LiveData<List<BluetoothDevice>> = _deviceList

    private val _scannerWarning = SingleEventLiveData<String>()
    val scannerWarning: LiveData<String> = _scannerWarning

    private val _startScanFlag = SingleEventLiveData<Boolean>()
    val startScanFlag: LiveData<Boolean> = _startScanFlag

    private val _message = SingleEventLiveData<String>()
    val message: LiveData<String> = _message

    private val _scanProgress = MutableLiveData(false)
    val scanProgress: LiveData<Boolean> = _scanProgress

    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private val foundDevices = HashMap<String, BluetoothDevice>()

    private val setDevicesToListTimer = object : CountDownTimer(SCAN_PERIOD_IN_MILLIS, 3000) {
        override fun onTick(millisUntilFinished: Long) {
            _deviceList.postValue(foundDevices.values.toList())
        }

        override fun onFinish() {
        }
    }

    private var scanCallback: ScanCallback? = null


    init {
        _startScanFlag.value = true
    }

    private fun buildScanFilters(): List<ScanFilter>? {
        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(BleControlManager.getFilterServiceUuid())
            .build()
//        return listOf(scanFilter)
        return null
    }

    private fun buildScanSettings() = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    fun startScanning() {
        if (scanCallback != null) {
            _message.value = context.getString(R.string.device_scanner_bt_scanning)
            return
        }

        scanCallback = BleScanCallback()
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
            && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        ) {
            return
        }
        _scanProgress.postValue(true)
        handler.postDelayed({ stopScanning() }, SCAN_PERIOD_IN_MILLIS)
        bluetoothLeScanner?.startScan(buildScanFilters(), buildScanSettings(), scanCallback)

        setDevicesToListTimer.start()
    }

    fun stopScanning() {
        if (scanCallback == null) return

        _scanProgress.value = false
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
            && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        ) {
            return
        }
        bluetoothLeScanner?.stopScan(scanCallback)
        scanCallback = null

        setDevicesToListTimer.cancel()
    }

    inner class BleScanCallback : ScanCallback() {
        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { result ->
                foundDevices[result.device.address] = result.device
            }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            foundDevices[result.device.address] = result.device
        }

        override fun onScanFailed(errorCode: Int) {
            _scannerWarning.value = when (errorCode) {
                SCAN_FAILED_ALREADY_STARTED -> "SCAN_FAILED_ALREADY_STARTED"
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED"
                SCAN_FAILED_INTERNAL_ERROR -> "SCAN_FAILED_INTERNAL_ERROR"
                SCAN_FAILED_FEATURE_UNSUPPORTED -> "SCAN_FAILED_FEATURE_UNSUPPORTED"
                SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> "SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES"
                SCAN_FAILED_SCANNING_TOO_FREQUENTLY -> "SCAN_FAILED_SCANNING_TOO_FREQUENTLY"
                else -> "NO_ERROR"
            }
        }
    }

}