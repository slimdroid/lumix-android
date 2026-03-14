package com.slimdroid.lumix.core.bluetooth.connector

sealed interface BleConnectionResult {
    data object Success : BleConnectionResult
    sealed interface Failed : BleConnectionResult {
        data object BluetoothNotInitialized : Failed
        data object DeviceNotFound : Failed
    }
}