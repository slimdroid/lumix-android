package com.slimdroid.lumix.scanner.http

import com.slimdroid.lumix.scanner.Device
import com.slimdroid.lumix.scanner.dto.asExternalModel
import com.slimdroid.lumix.scanner.http.source.ScanDeviceDataSource

internal class DeviceProvider(
    private val datasource: ScanDeviceDataSource
) {

    suspend fun getDevice(): Result<Device> = runCatching {
        datasource.getDevice().asExternalModel()
    }

}