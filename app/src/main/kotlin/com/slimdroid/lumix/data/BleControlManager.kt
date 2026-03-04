package com.slimdroid.lumix.data

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.content.Context
import android.os.ParcelUuid
import androidx.lifecycle.MutableLiveData
import com.slimdroid.lumix.utils.SingleEventLiveData
import java.util.*

private val SERVICE_CONNECTION_UUID = UUID.fromString("459aa3b5-52c3-4d75-a64b-9cd76f65cfbb")
private val SERVICE_WORK_TIME_UUID = UUID.fromString("cafa0333-8a16-4a59-b706-2f0e3fd38f58")

private val CHARACTERISTIC_CONNECTION_CREDENTIALS_UUID =
    UUID.fromString("b9e70f80-d55e-4cd7-bec6-14be34590efc")
private val CHARACTERISTIC_CONNECTION_RESPONSE_UUID =
    UUID.fromString("7048479a-23f2-4f5b-8113-e60e59294b5a")

private val CHARACTERISTIC_WORK_TIME_UUID = UUID.fromString("2c1529cd-f45d-4739-9738-2886fe46f7f1")
private val DESCRIPTION_WORK_TIME_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")


const val RESPONSE_CONNECT_SUCCESS = 0
const val RESPONSE_WRONG_PASSWORD = 1
const val RESPONSE_UNKNOWN_ERROR = 2

@SuppressLint("MissingPermission")
class BleControlManager(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter?
) {

    val workedTimeLiveData = MutableLiveData<Int>()
    val messageLiveData = SingleEventLiveData<String>()

    var isConnected = false
        private set

    private var bluetoothGatt: BluetoothGatt? = null
    private var device: BluetoothDevice? = null
    private var connectionRequestCharacteristic: BluetoothGattCharacteristic? = null
    private var connectionResponseCharacteristic: BluetoothGattCharacteristic? = null
    private var workedTimeCharacteristic: BluetoothGattCharacteristic? = null

    companion object {
        fun getFilterServiceUuid() = ParcelUuid(SERVICE_CONNECTION_UUID)
    }

    private var bleControlCallback: BleControlCallback? = null

    interface BleControlCallback {
        fun onDeviceConnected(device: BluetoothDevice?)
        fun onDeviceFailedToConnect(device: BluetoothDevice?)
        fun onDeviceDisconnected(device: BluetoothDevice?)

        fun onWiFiConnectionResponse(status: Int)
    }

    fun registerBleControlCallback(bleControlCallback: BleControlCallback) {
        this.bleControlCallback = bleControlCallback
    }

    fun connect(address: String): Boolean {
        bluetoothAdapter?.let { adapter ->
            return try {
                adapter.getRemoteDevice(address).also {
                    device = it
                    bluetoothGatt = it.connectGatt(context, false, bluetoothGattCallback)
                }
                true
            } catch (exception: IllegalArgumentException) {
                messageLiveData.value = "Device not found with provided address. Unable to connect."
                bleControlCallback?.onDeviceFailedToConnect(device)
                false
            }
        } ?: run {
            messageLiveData.value = "BluetoothAdapter not initialized"
            bleControlCallback?.onDeviceFailedToConnect(device)
            return false
        }
    }

    fun disconnect() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            device = null
            bluetoothGatt = null
            connectionRequestCharacteristic = null
            connectionResponseCharacteristic = null
            workedTimeCharacteristic = null
        }
    }

    fun sendCredentials(ssid: String, password: String) {
        if (!isConnected) return

        connectionRequestCharacteristic?.setValue(createRequestString(ssid, password))
        bluetoothGatt?.writeCharacteristic(connectionRequestCharacteristic)
    }


    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                STATE_CONNECTED -> {
                    messageLiveData.postValue("Successfully connected to the GATT Server")
                    bluetoothGatt?.discoverServices()
                }
                STATE_DISCONNECTED -> {
                    messageLiveData.postValue("Disconnected from the GATT Server")
                    if (isConnected) {
                        bleControlCallback?.onDeviceDisconnected(device)
                    } else {
                        bleControlCallback?.onDeviceFailedToConnect(device)
                    }
                    isConnected = false
                }
            }
        }

        override fun onServicesDiscovered(bluetoothGatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGatt?.let { gatt ->
                    isConnected = true
                    bleControlCallback?.onDeviceConnected(device)

                    // Service work time
                    gatt.getService(SERVICE_WORK_TIME_UUID)?.apply {
                        workedTimeCharacteristic = getCharacteristic(CHARACTERISTIC_WORK_TIME_UUID)
                        gatt.setCharacteristicNotification(workedTimeCharacteristic, true)

                        val descriptor =
                            workedTimeCharacteristic?.getDescriptor(DESCRIPTION_WORK_TIME_UUID)
                        descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    }

                    // Service registration
                    gatt.getService(SERVICE_CONNECTION_UUID)?.apply {
                        connectionRequestCharacteristic =
                            getCharacteristic(CHARACTERISTIC_CONNECTION_CREDENTIALS_UUID)

                        connectionResponseCharacteristic =
                            getCharacteristic(CHARACTERISTIC_CONNECTION_RESPONSE_UUID)
                    }
                }
            } else {
                messageLiveData.postValue("onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            messageLiveData.postValue("Characteristic was read with status: $status")
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            messageLiveData.postValue("Characteristic was write with status: $status")
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            gattCharacteristic: BluetoothGattCharacteristic?
        ) {
            gattCharacteristic?.let { characteristic ->
                when (characteristic.uuid) {
                    CHARACTERISTIC_CONNECTION_CREDENTIALS_UUID -> {

                    }
                    CHARACTERISTIC_CONNECTION_RESPONSE_UUID ->
                        characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                            ?.let { status ->
                                bleControlCallback?.onWiFiConnectionResponse(status)
                            }
                    CHARACTERISTIC_WORK_TIME_UUID ->
                        characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                            ?.let { time ->
                                workedTimeLiveData.postValue(time)
                            }
                    else -> {}
                }
            }
        }
    }

    private fun createRequestString(ssid: String, password: String): String {
        return "Credentials:\n\nSSID: $ssid\nPassword: $password"
    }

}