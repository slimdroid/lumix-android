package com.slimdroid.lumix.core.scanner

import com.slimdroid.lumix.core.model.LumixDevice

data class Device(
    val ipAddress: String,
    val macAddress: String,
    val name: String,
    val firmware: String,
    val type: DeviceType,
) {

    override fun equals(other: Any?) = (other is Device)
            && ipAddress == other.ipAddress
            && macAddress == other.macAddress
            && firmware == other.firmware
            && type == other.type

    override fun hashCode(): Int = macAddress.hashCode()

}

fun Device.toExternalModel() = LumixDevice(
    ipAddress = ipAddress,
    macAddress = macAddress,
    name = name,
    firmware = firmware,
    type = type.name
)