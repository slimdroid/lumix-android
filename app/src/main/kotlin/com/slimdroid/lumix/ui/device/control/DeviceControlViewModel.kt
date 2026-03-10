package com.slimdroid.lumix.ui.device.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slimdroid.lumix.data.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceControlViewModel : ViewModel() {

    private val sender = MessageSender

    init {
        viewModelScope.launch(Dispatchers.IO) {
            sender.start()
        }
    }

    fun powerToggle(isPowerOn: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            sender.sendMessage("{\"cmd\":\"set_power\",\"state\":${if (isPowerOn) 1 else 0}}")
        }
    }

    fun setBrightness(brightness: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            sender.sendMessage("{\"cmd\":\"set_brightness\",\"value\":$brightness}")
        }
    }

    fun setLedCount(ledCount: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = ledCount.toIntOrNull() ?: return@launch
            sender.sendMessage("{\"cmd\":\"set_led_count\",\"value\":$count}")
        }
    }

    private var currentEffect: Int = 0
    fun setNextEffect() {
        viewModelScope.launch(Dispatchers.IO) {
            sender.sendMessage("{\"cmd\":\"set_mode\",\"mode\":${++currentEffect}}")
        }
    }

    fun setPreviousEffect() {
        viewModelScope.launch(Dispatchers.IO) {
            sender.sendMessage("{\"cmd\":\"set_mode\",\"mode\":${--currentEffect}}")
        }
    }

}