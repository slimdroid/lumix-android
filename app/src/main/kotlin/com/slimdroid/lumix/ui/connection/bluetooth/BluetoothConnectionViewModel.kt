package com.slimdroid.lumix.ui.connection.bluetooth

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.slimdroid.lumix.data.BleControlManager
import com.slimdroid.lumix.data.RESPONSE_CONNECT_SUCCESS
import com.slimdroid.lumix.data.RESPONSE_UNKNOWN_ERROR
import com.slimdroid.lumix.data.RESPONSE_WRONG_PASSWORD
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class BluetoothConnectionViewModel(
    private val bleControlManager: BleControlManager,
    private val deviceAddress: String
) : ViewModel() {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    val scannerWarning: LiveData<String> = bleControlManager.messageLiveData

    val workedTime: LiveData<String> = bleControlManager
        .workedTimeLiveData
        .map {
            timeFormatter.format(
                LocalDateTime.ofEpochSecond(it.toLong(), 0, ZoneOffset.UTC)
            )
        }

    var ssid: String = ""
    var password: String = ""

    val address: LiveData<String> = MutableLiveData(deviceAddress)

    private val _connectingProgress = MutableLiveData(false)
    val connectingProgress: LiveData<Boolean> = _connectingProgress

    private val _messageFromDevice = MutableLiveData<String>()
    val messageFromDevice: LiveData<String> = _messageFromDevice


    private val bleControlCallback = object : BleControlManager.BleControlCallback {
        override fun onDeviceConnected(device: BluetoothDevice?) {
            _connectingProgress.postValue(false)
        }

        override fun onDeviceFailedToConnect(device: BluetoothDevice?) {
            _connectingProgress.postValue(false)
        }

        override fun onDeviceDisconnected(device: BluetoothDevice?) {
            _connectingProgress.postValue(false)
        }

        override fun onWiFiConnectionResponse(status: Int) {
            when (status) {
                RESPONSE_CONNECT_SUCCESS -> {
                    // TODO подключиться к девайсу в локальной сите и отключить BLE
                }
                RESPONSE_WRONG_PASSWORD -> {
                    // TODO запросить ввести пароль еще раз
                }
                RESPONSE_UNKNOWN_ERROR -> {
                    // TODO сказать что произошла какая-то интересная ошибка при подключении в локальную сеть
                }
            }
        }

    }

    init {
        bleControlManager.registerBleControlCallback(bleControlCallback)
    }

    fun disconnect() {
        bleControlManager.disconnect()
    }

    fun connect() {
        bleControlManager.connect(deviceAddress)
        _connectingProgress.value = true
    }

    fun sendCredentialsRequest() {
        bleControlManager.sendCredentials(ssid, password)
    }

}