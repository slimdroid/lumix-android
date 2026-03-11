package com.slimdroid.lumix.scanner

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