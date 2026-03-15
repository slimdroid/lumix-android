package com.slimdroid.lumix.core.bluetooth.connector

sealed interface CredentialsResult {
    data object Success : CredentialsResult
    data object WrongPassword : CredentialsResult
    data object Failure : CredentialsResult
    data object BluetoothNotConnected : CredentialsResult
    data object Timeout : CredentialsResult
}