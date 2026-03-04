package com.slimdroid.lumix.ui.device_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DeviceListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}