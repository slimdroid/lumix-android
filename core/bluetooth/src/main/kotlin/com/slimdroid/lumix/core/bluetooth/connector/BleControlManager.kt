package com.slimdroid.lumix.core.bluetooth.connector

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.content.Context
import android.os.ParcelUuid
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID
import javax.inject.Inject

private val SERVICE_CONNECTION_UUID = UUID.fromString("459aa3b5-52c3-4d75-a64b-9cd76f65cfbb")
private val CREDENTIALS_UUID = UUID.fromString("b9e70f80-d55e-4cd7-bec6-14be34590efc")
private val RESPONSE_UUID = UUID.fromString("7048479a-23f2-4f5b-8113-e60e59294b5a")

const val RESPONSE_CONNECT_SUCCESS = 0
const val RESPONSE_WRONG_PASSWORD = 1
const val RESPONSE_UNKNOWN_ERROR = 2

interface BleControlManager {

    val bleStatus: SharedFlow<BleConnectionState>

    fun connect(macAddress: String): BleConnectionResult

    fun disconnect()

    fun sendCredentials(ssid: String, password: String)
}

@SuppressLint("MissingPermission")
class BleControlManagerImpl @Inject constructor(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter?,
) : BleControlManager {

    private val _bleStatus: MutableSharedFlow<BleConnectionState> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
    )
    override val bleStatus: SharedFlow<BleConnectionState> = _bleStatus.asSharedFlow()

    private var isConnected = false

    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionRequest: BluetoothGattCharacteristic? = null
    private var connectionResponse: BluetoothGattCharacteristic? = null

    override fun connect(macAddress: String): BleConnectionResult = runCatching {
        bluetoothAdapter?.getRemoteDevice(macAddress)?.also {
            bluetoothGatt = it.connectGatt(context, false, bluetoothGattCallback)
        } ?: return@runCatching BleConnectionResult.Failed.BluetoothNotInitialized
    }.fold(
        onSuccess = { BleConnectionResult.Success },
        onFailure = { BleConnectionResult.Failed.DeviceNotFound }
    )

    override fun disconnect() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
            connectionRequest = null
            connectionResponse = null
        }
    }

    override fun sendCredentials(ssid: String, password: String) {
        if (!isConnected) return

        connectionRequest?.setValue(createRequestString(ssid, password))
        bluetoothGatt?.writeCharacteristic(connectionRequest)
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                STATE_CONNECTED -> {
                    bluetoothGatt?.discoverServices()
                }

                STATE_DISCONNECTED -> {
                    if (isConnected) {
                        _bleStatus.tryEmit(BleConnectionState.Disconnected)
                    } else {
                        _bleStatus.tryEmit(BleConnectionState.FailedToConnect)
                    }
                    isConnected = false
                }
            }
        }

        override fun onServicesDiscovered(bluetoothGatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGatt?.let { gatt ->
                    isConnected = true
                    _bleStatus.tryEmit(BleConnectionState.Connected)

                    // Service registration
                    gatt.getService(SERVICE_CONNECTION_UUID)?.apply {
                        connectionRequest = getCharacteristic(CREDENTIALS_UUID)
                        connectionResponse = getCharacteristic(RESPONSE_UUID)
                    }
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            gattCharacteristic: BluetoothGattCharacteristic?
        ) {
            gattCharacteristic?.let { characteristic ->
                when (characteristic.uuid) {
                    CREDENTIALS_UUID -> {

                    }

                    RESPONSE_UUID ->
                        characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                            ?.let { status ->
                                // TODO inform Wi-Fi connection status
                            }

                    else -> {}
                }
            }
        }
    }

    private fun createRequestString(ssid: String, password: String): String = "$ssid:$password"

    companion object {
        fun getFilterServiceUuid(): ParcelUuid = ParcelUuid(SERVICE_CONNECTION_UUID)
    }

}