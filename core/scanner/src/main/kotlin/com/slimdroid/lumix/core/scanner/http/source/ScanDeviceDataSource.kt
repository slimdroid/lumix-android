package com.slimdroid.lumix.core.scanner.http.source

import com.slimdroid.lumix.core.scanner.dto.ScanDeviceDto
import com.slimdroid.lumix.core.scanner.http.ScannerClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ScanDeviceDataSource(
    deviceIp: String,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    companion object {
        private const val DEVICE = "api/device"
    }

    private val deviceApi = ScannerClient.getHttpClient(deviceIp)

    suspend fun getDevice(): ScanDeviceDto = withContext(ioDispatcher) {
        deviceApi.use {
            it.get(DEVICE).body()
        }
    }

}