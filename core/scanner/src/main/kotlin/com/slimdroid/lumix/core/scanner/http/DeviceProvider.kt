package com.slimdroid.lumix.core.scanner.http

import com.slimdroid.lumix.core.scanner.Device
import com.slimdroid.lumix.core.scanner.dto.asExternalModel
import com.slimdroid.lumix.core.scanner.http.source.ScanDeviceDataSource

internal class DeviceProvider(
    private val datasource: ScanDeviceDataSource
) {

    suspend fun getDevice(): Result<Device> = runCatching {
        datasource.getDevice().asExternalModel()
    }

}