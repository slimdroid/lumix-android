package com.slimdroid.lumix.ui.connection.wifi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.slimdroid.lumix.App
import com.slimdroid.lumix.dependency.Dependencies

class WifiScanningViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WifiScanningViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WifiScanningViewModel(App.instance, Dependencies.wifiManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}