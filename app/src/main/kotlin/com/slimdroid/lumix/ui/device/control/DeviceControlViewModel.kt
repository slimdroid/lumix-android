package com.slimdroid.lumix.ui.device.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slimdroid.lumix.data.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceControlViewModel : ViewModel() {

    private val sender = MessageSender

    init {
        viewModelScope.launch {
            sender.start()
        }
    }

    fun powerToggle(isPowerOn: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            sender.sendMessage("")
        }
    }

    fun setBrightness(brightness: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            sender.sendMessage("")
        }
    }

}