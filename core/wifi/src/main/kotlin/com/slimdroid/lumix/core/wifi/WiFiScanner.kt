package com.slimdroid.lumix.core.wifi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface WiFiScanner {

    fun startScan(): Flow<List<ScanResult>>
    fun stopScan()
}

internal class WiFiScannerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wifiManager: WifiManager
) : WiFiScanner {

    private var wifiScanReceiver: BroadcastReceiver? = null

    @SuppressLint("MissingPermission")
    override fun startScan(): Flow<List<ScanResult>> = callbackFlow {
        Log.d(TAG, "startScan: starting...")

        if (!wifiManager.isWifiEnabled) {
            if (wifiManager.wifiState != WifiManager.WIFI_STATE_ENABLING) {
                wifiManager.isWifiEnabled = true
            }
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                Log.d(TAG, "onReceive: scan results available, success=$success")
                val scanResults = wifiManager.scanResults
                if (scanResults != null) {
                    trySend(filterScanResult(scanResults.toMutableList()))
                }
            }
        }

        wifiScanReceiver = receiver
        context.registerReceiver(
            receiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )

        val initialScanStarted = wifiManager.startScan()
        Log.d(TAG, "startScan: wifiManager.startScan() called, success=$initialScanStarted")
        if (!initialScanStarted) {
            val scanResults = wifiManager.scanResults
            if (scanResults != null) {
                trySend(filterScanResult(scanResults.toMutableList()))
            }
        }

        awaitClose {
            Log.d(TAG, "startScan: closing flow...")
            stopScan()
        }
    }

    override fun stopScan() {
        Log.d(TAG, "stopScan: stopping scan...")
        wifiScanReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) {
                Log.e(TAG, "stopScan: error unregistering receiver", e)
            }
            wifiScanReceiver = null
        }
    }

    @Suppress("DEPRECATION")
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

    companion object {
        private const val TAG = "WiFiScanner"
        private const val FREQUENCY_LIMIT = 2412
    }
}