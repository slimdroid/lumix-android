package com.slimdroid.lumix.core.scanner.http

import androidx.annotation.CheckResult
import com.slimdroid.lumix.core.scanner.Device
import com.slimdroid.lumix.core.scanner.Scanner
import com.slimdroid.lumix.core.scanner.http.source.ScanDeviceDataSource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow

abstract class HttpScanner : Scanner {

    internal val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val TAG = "Scanner_HTTP"

        @CheckResult
        suspend fun checkDevice(deviceIp: String): Result<Device> {
            val repository = DeviceProvider(ScanDeviceDataSource(deviceIp))
            val result = repository.getDevice()
            if (result.isFailure) {
                Napier.i("unknown device, ip:$deviceIp", tag = TAG)
            }
            return result
        }
    }

    abstract override fun startScan(): Flow<Device>

    abstract override fun stopScan()

}