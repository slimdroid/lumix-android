package com.slimdroid.lumix.ui.connection.wifi

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.slimdroid.lumix.utils.SingleEventLiveData

private const val FREQUENCY_LIMIT = 3000

class WifiScanningViewModel(
    application: Application,
    private val wifiManager: WifiManager
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _wifiList = MutableLiveData<List<ScanResult>>()
    val wifiList: LiveData<List<ScanResult>> = _wifiList

    private val _scannerWarning = SingleEventLiveData<String>()
    val scannerWarning: LiveData<String> = _scannerWarning

    private val _startScanFlag = SingleEventLiveData<Boolean>()
    val startScanFlag: LiveData<Boolean> = _startScanFlag

    private val _scanProgress = MutableLiveData(false)
    val scanProgress: LiveData<Boolean> = _scanProgress

    private val _message = SingleEventLiveData<String>()
    val message: LiveData<String> = _message

    init {
        _startScanFlag.value = true
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            _scanProgress.value = false
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) scanSuccess() else scanFailure()
        }
    }

    fun startScanning() {
        _scanProgress.value = true
        if (!wifiManager.isWifiEnabled)
            if (wifiManager.wifiState != WifiManager.WIFI_STATE_ENABLING)
                wifiManager.isWifiEnabled = true

        context.registerReceiver(
            wifiScanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
        if (!wifiManager.startScan()) scanFailure()
    }

    private fun scanSuccess() {
        _wifiList.value = wifiManager.scanResults?.let {
            filterScanResult(it)
        } ?: emptyList()
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        _wifiList.value = wifiManager.scanResults?.let {
            filterScanResult(it)
        } ?: emptyList()
    }

    private fun filterScanResult(scanResults: MutableList<ScanResult>): List<ScanResult> {
        val linkedMap = LinkedHashMap<String, ScanResult>(scanResults.size)

        for (result in scanResults) {
            if ((result.frequency > FREQUENCY_LIMIT) || result.SSID == "") continue
            if (linkedMap.containsKey(result.SSID)) {
                if (result.level > linkedMap[result.SSID]!!.level) {
                    linkedMap[result.SSID] = result
                }
                continue
            }
            linkedMap[result.SSID] = result
        }
        return scanResults
            .apply {
                clear()
                addAll(linkedMap.values)
            }
            .sortedBy { it.level }
            .reversed()
    }

    override fun onCleared() {
        context.unregisterReceiver(wifiScanReceiver)
    }

}