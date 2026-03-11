package com.slimdroid.lumix.core.model

data class LumixDevice(
    val macAddress: String,
    val ipAddress: String,
    val name: String,
    val type: String,
    val firmware: String,
    val online: Boolean = false
) {
    override fun equals(other: Any?) = (other is LumixDevice)
            && macAddress == other.macAddress
            && ipAddress == other.ipAddress
            && name == other.name
            && type == other.type
            && firmware == other.firmware

    override fun hashCode(): Int {
        var result = online.hashCode()
        result = 31 * result + macAddress.hashCode()
        result = 31 * result + ipAddress.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + firmware.hashCode()
        return result
    }
}