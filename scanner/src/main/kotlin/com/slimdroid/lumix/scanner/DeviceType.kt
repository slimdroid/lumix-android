package com.slimdroid.lumix.scanner

enum class DeviceType {
    UNKNOWN,
    LUMIX;

    companion object {
        fun valueOfString(value: String): DeviceType = entries.firstOrNull { it.name == value } ?: UNKNOWN
    }
}