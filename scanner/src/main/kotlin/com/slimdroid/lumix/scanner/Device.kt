package com.slimdroid.lumix.scanner

data class Device(
    val deviceIp: String,
    val deviceId: String,
    val name: String,
    val appVersion: String,
    val type: DeviceType,
) {

    override fun equals(other: Any?) = (other is Device)
            && deviceIp == other.deviceIp
            && deviceId == other.deviceId
            && type == other.type

    override fun hashCode(): Int = deviceId.hashCode()

}