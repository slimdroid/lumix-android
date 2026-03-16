package com.slimdroid.lumix.core.bluetooth.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import android.util.Log
import com.slimdroid.lumix.core.bluetooth.connector.SERVICE_CONNECTION_UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

interface BluetoothScanner {

    fun startScan(): Flow<ScannerResult>

    fun stopScan()
}

internal class BluetoothScannerImpl @Inject constructor(
    private val bluetoothLeScanner: BluetoothLeScanner?
) : BluetoothScanner {

    private var scanCallback: ScanCallback? = null

    private val foundDevices = HashMap<String, BluetoothDevice>()

    @SuppressLint("MissingPermission")
    override fun startScan(): Flow<ScannerResult> = callbackFlow {
        Log.d(TAG, "startScan: starting...")
        foundDevices.clear()

        val callback = object : ScanCallback() {
            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                Log.d(TAG, "onBatchScanResults: found ${results.size} devices")
                results.forEach { result ->
                    foundDevices[result.device.address] = result.device
                }
                trySend(ScannerResult.Success(foundDevices.values.toList()))
            }

            override fun onScanResult(callbackType: Int, result: ScanResult) {
                Log.d(TAG, "onScanResult: device found address=${result.device.address}")
                foundDevices[result.device.address] = result.device
                trySend(ScannerResult.Success(foundDevices.values.toList()))
            }

            override fun onScanFailed(errorCode: Int) {
                val fail = when (errorCode) {
                    SCAN_FAILED_ALREADY_STARTED -> ScannerResult.Failure.ScanFailedAlreadyStarted
                    SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> ScannerResult.Failure.ScanFailedApplicationRegistrationFailed
                    SCAN_FAILED_INTERNAL_ERROR -> ScannerResult.Failure.ScanFailedInternalError
                    SCAN_FAILED_FEATURE_UNSUPPORTED -> ScannerResult.Failure.ScanFailedFeatureUnsupported
                    SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> ScannerResult.Failure.ScanFailedOutOfHardwareResources
                    SCAN_FAILED_SCANNING_TOO_FREQUENTLY -> ScannerResult.Failure.ScanFailedScanningTooFrequently
                    else -> ScannerResult.Failure.ScanFailedInternalError
                }
                Log.d(TAG, "onScanFailed: errorCode=$errorCode, fail=$fail")
                trySend(fail)
            }
        }

        scanCallback = callback

        bluetoothLeScanner?.startScan(buildScanFilters(), buildScanSettings(), callback)
            ?: run {
                Log.d(TAG, "startScan: bluetooth is not supported")
                trySend(ScannerResult.Failure.BluetoothIsNotSupported)
                close()
            }

        val timeoutJob = launch {
            delay(SCAN_TIMEOUT_MILLIS)
            Log.d(TAG, "startScan: timeout reached, stopping...")
            close()
        }

        awaitClose {
            Log.d(TAG, "startScan: closing flow...")
            timeoutJob.cancel()
            stopScan()
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        Log.d(TAG, "stopScan: stop scan called")
        scanCallback?.let {
            bluetoothLeScanner?.stopScan(it)
            scanCallback = null
        }
    }

    private fun buildScanFilters(): List<ScanFilter> {
        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(SERVICE_CONNECTION_UUID))
            .build()
        return listOf(scanFilter)
    }

    private fun buildScanSettings() = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private companion object {
        private const val TAG = "BluetoothScanner"
        val SCAN_TIMEOUT_MILLIS = 30.seconds
    }

}