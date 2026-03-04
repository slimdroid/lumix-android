package com.slimdroid.lumix.ui.connection.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.slimdroid.lumix.App
import com.slimdroid.lumix.dependency.Dependencies

class BluetoothScannerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceScannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceScannerViewModel(
                App.instance,
                Dependencies.bluetoothAdapter
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}