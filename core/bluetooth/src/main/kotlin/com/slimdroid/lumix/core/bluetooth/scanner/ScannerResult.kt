package com.slimdroid.lumix.core.bluetooth.scanner

import android.bluetooth.BluetoothDevice

sealed interface ScannerResult {
    data class Success(val devices: List<BluetoothDevice>) : ScannerResult
    sealed interface Failure : ScannerResult {
        data object ScanFailedAlreadyStarted : Failure
        data object ScanFailedApplicationRegistrationFailed : Failure
        data object ScanFailedInternalError : Failure
        data object ScanFailedFeatureUnsupported : Failure
        data object ScanFailedOutOfHardwareResources : Failure
        data object ScanFailedScanningTooFrequently : Failure
        data object BluetoothIsNotSupported : Failure
    }
}