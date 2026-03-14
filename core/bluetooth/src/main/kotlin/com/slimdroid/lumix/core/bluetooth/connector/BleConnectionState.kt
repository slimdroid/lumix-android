package com.slimdroid.lumix.core.bluetooth.connector

sealed interface BleConnectionState {
    data object FailedToConnect : BleConnectionState
    data object Disconnected : BleConnectionState
    data object Connected : BleConnectionState
}