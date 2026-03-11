package com.slimdroid.lumix.core.scanner.dto

import com.slimdroid.lumix.core.scanner.DeviceType
import com.slimdroid.lumix.core.scanner.Device
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ScanDeviceDto(
    @SerialName("device_id")        val macAddress: String,
    @SerialName("name")             val name: String,
    @SerialName("type")             val type: String,
    @SerialName("ip")               val ipAddress: String,
    @SerialName("app_version")      val firmware: String,
)

internal fun ScanDeviceDto.asExternalModel() = Device(
    ipAddress = ipAddress,
    macAddress = macAddress.uppercase(),
    type = DeviceType.valueOfString(type),
    name = name,
    firmware = firmware
)