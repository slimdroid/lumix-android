package com.slimdroid.lumix.scanner.dto

import com.slimdroid.lumix.scanner.DeviceType
import com.slimdroid.lumix.scanner.Device
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ScanDeviceDto(
    @SerialName("device_id")        val id: String,
    @SerialName("name")             val name: String,
    @SerialName("type")             val type: String,
    @SerialName("ip")               val ip: String,
    @SerialName("app_version")      val appVersion: String,
)

internal fun ScanDeviceDto.asExternalModel() = Device(
    deviceIp = ip,
    deviceId = id.uppercase(),
    type = DeviceType.valueOfString(type),
    name = name,
    appVersion = appVersion
)