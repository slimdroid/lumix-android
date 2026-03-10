package com.slimdroid.lumix.core.model

data class LumixDeviceState(
    val device: LumixDevice,
    val brightness: UByte,
    val online: Boolean = device.online
)