package com.slimdroid.lumix.core.bluetooth.connector

sealed interface ConnectionState {
    data object FailedToConnect : ConnectionState
    data object Disconnected : ConnectionState
    data object Disconnecting : ConnectionState
    data object Connected : ConnectionState
    data object Connecting : ConnectionState
}