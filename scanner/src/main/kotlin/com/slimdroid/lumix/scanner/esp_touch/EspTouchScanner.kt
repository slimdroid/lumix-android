package com.slimdroid.lumix.scanner.esp_touch

import android.content.Context
import android.util.Log
import com.espressif.iot.esptouch.EsptouchTask
import com.espressif.iot.esptouch.IEsptouchResult
import com.slimdroid.lumix.scanner.Device
import com.slimdroid.lumix.scanner.Scanner
import com.slimdroid.lumix.scanner.http.HttpScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

/**
 * This scanner uses EspTouch library for Android
 * @see <a href="https://github.com/EspressifApp/EsptouchForAndroid/tree/master/esptouch">ESP Touch GitHub</a>
 */
class EspTouchScanner(
    context: Context,
    ssid: String,
    bssid: String,
    password: String,
    private val expectCount: Int = 1
) : Scanner {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var esptouchTask: EsptouchTask =
        EsptouchTask(ssid, bssid, password, context).apply {
            setPackageBroadcast(true)
        }
    private var producerScope: ProducerScope<Device>? = null
    private var isScanning = false

    companion object {
        private const val TAG = "Scanner ESP Touch"
    }

    override fun startScan(): Flow<Device> = channelFlow {
        if (!isScanning) {
            Log.d(TAG, "Start ESP Touch scanner")

            isScanning = true
            producerScope = this

            coroutineScope.launch {
                val resultList: List<IEsptouchResult> = esptouchTask.executeForResults(expectCount)

                resultList.forEach { result ->
                    if (result.isSuc) {
                        result.inetAddress.hostAddress?.let { address ->
                            HttpScanner.checkDevice(address).onSuccess { trySend(it) }
                        }
                    }
                }
                stopScan()
            }
        }
        awaitClose()
    }.takeWhile {
        isScanning
    }.onEach {
        Log.d(TAG, "device:${it.macAddress}, ip:${it.ipAddress}, type:${it.type}")
    }.onCompletion {
        Log.d(TAG, "result: Broadcast search is over")
    }

    override fun stopScan() {
        if (!esptouchTask.isCancelled) esptouchTask.interrupt()

        isScanning = false
        producerScope?.close()
        producerScope = null
    }

}