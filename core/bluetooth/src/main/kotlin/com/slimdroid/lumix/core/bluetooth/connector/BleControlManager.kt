package com.slimdroid.lumix.core.bluetooth.connector

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_CONNECTING
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTING
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID
import javax.inject.Inject

internal val SERVICE_CONNECTION_UUID = UUID.fromString("459aa3b5-52c3-4d75-a64b-9cd76f65cfbb")
private val CREDENTIALS_UUID = UUID.fromString("b9e70f80-d55e-4cd7-bec6-14be34590efc")
private val RESPONSE_UUID = UUID.fromString("7048479a-23f2-4f5b-8113-e60e59294b5a")

private const val RESPONSE_CONNECT_SUCCESS = 0
private const val RESPONSE_WRONG_CREDENTIALS = 1
private const val RESPONSE_FAILURE = 2

interface BleControlManager {

    val bleStatus: SharedFlow<ConnectionState>

    fun connect(macAddress: String): Boolean

    fun disconnect()

    suspend fun sendCredentials(ssid: String, password: String): CredentialsResult
}

@SuppressLint("MissingPermission")
class BleControlManagerImpl @Inject constructor(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter?,
) : BleControlManager {

    private val _bleStatus: MutableSharedFlow<ConnectionState> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
    )
    override val bleStatus: SharedFlow<ConnectionState> = _bleStatus.asSharedFlow()

    private var isConnected = false

    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionRequest: BluetoothGattCharacteristic? = null
    private var connectionResponse: BluetoothGattCharacteristic? = null
    private var pendingCredentialsRequest: CompletableDeferred<CredentialsResult>? = null

    override fun connect(macAddress: String): Boolean = runCatching {
        Log.d(TAG, "connect: macAddress=$macAddress")
        bluetoothAdapter?.getRemoteDevice(macAddress)?.also {
            bluetoothGatt = it.connectGatt(context, false, bluetoothGattCallback)
        } ?: run {
            Log.d(TAG, "connect: bluetoothAdapter not initialized")
            return@runCatching false
        }
    }.fold(
        onSuccess = {
            Log.d(TAG, "connect: success")
            true
        },
        onFailure = {
            Log.d(TAG, "connect: failure", it)
            false
        }
    )

    override fun disconnect() {
        Log.d(TAG, "disconnect")
        bluetoothGatt?.let { gatt ->
            gatt.disconnect()
            gatt.close()
            bluetoothGatt = null
            connectionRequest = null
            connectionResponse = null
            isConnected = false
        }
    }

    override suspend fun sendCredentials(ssid: String, password: String): CredentialsResult {
        Log.d(TAG, "sendCredentials: ssid=$ssid")
        if (!isConnected) return CredentialsResult.BluetoothNotConnected

        val gatt = bluetoothGatt ?: return CredentialsResult.BluetoothNotConnected
        val characteristic = connectionRequest ?: return CredentialsResult.BluetoothNotConnected
        val value = createRequestString(ssid, password).toByteArray()

        val deferred = CompletableDeferred<CredentialsResult>()
        pendingCredentialsRequest = deferred

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeCharacteristic(
                    characteristic,
                    value,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            } else {
                @Suppress("DEPRECATION")
                characteristic.setValue(value)
                @Suppress("DEPRECATION")
                gatt.writeCharacteristic(characteristic)
            }

            withTimeoutOrNull(10000) {
                deferred.await()
            } ?: run {
                Log.d(TAG, "sendCredentials: timeout")
                CredentialsResult.Timeout
            }
        } finally {
            pendingCredentialsRequest = null
        }
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange: status=$status, newState=$newState")
            when (newState) {
                STATE_CONNECTING -> {
                    Log.d(TAG, "onConnectionStateChange: connecting")
                    _bleStatus.tryEmit(ConnectionState.Connecting)
                }

                STATE_CONNECTED -> {
                    Log.d(TAG, "onConnectionStateChange: connected")
                    bluetoothGatt?.discoverServices()
                }

                STATE_DISCONNECTING -> {
                    Log.d(TAG, "onConnectionStateChange: disconnecting")
                    _bleStatus.tryEmit(ConnectionState.Disconnecting)
                }

                STATE_DISCONNECTED -> {
                    if (isConnected) {
                        Log.d(TAG, "onConnectionStateChange: disconnected")
                        _bleStatus.tryEmit(ConnectionState.Disconnected)
                    } else {
                        Log.d(TAG, "onConnectionStateChange: failed to connect")
                        _bleStatus.tryEmit(ConnectionState.FailedToConnect)
                    }
                    isConnected = false
                }
            }
        }

        override fun onServicesDiscovered(bluetoothGatt: BluetoothGatt?, status: Int) {
            Log.d(TAG, "onServicesDiscovered: status=$status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGatt?.let { gatt ->
                    isConnected = true
                    _bleStatus.tryEmit(ConnectionState.Connected)

                    // Service registration
                    gatt.getService(SERVICE_CONNECTION_UUID)?.apply {
                        connectionRequest = getCharacteristic(CREDENTIALS_UUID)
                        connectionResponse = getCharacteristic(RESPONSE_UUID)
                    }
                }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            gattCharacteristic: BluetoothGattCharacteristic?
        ) {
            if (gatt != null && gattCharacteristic != null) {
                @Suppress("DEPRECATION")
                onCharacteristicChanged(gatt, gattCharacteristic, gattCharacteristic.value)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            gattCharacteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            Log.d(TAG, "onCharacteristicChanged: uuid=${gattCharacteristic.uuid}")
            when (gattCharacteristic.uuid) {
                CREDENTIALS_UUID -> {}

                RESPONSE_UUID -> {
                    val statusValue = if (value.size >= 4) {
                        (value[0].toInt() and 0xFF) or
                                (value[1].toInt() and 0xFF shl 8) or
                                (value[2].toInt() and 0xFF shl 16) or
                                (value[3].toInt() and 0xFF shl 24)
                    } else if (value.isNotEmpty()) {
                        value[0].toInt() and 0xFF
                    } else {
                        null
                    }
                    val result = when (statusValue) {
                        RESPONSE_CONNECT_SUCCESS -> {
                            Log.d(TAG, "onCharacteristicChanged: credentials accepted")
                            CredentialsResult.Success
                        }

                        RESPONSE_WRONG_CREDENTIALS -> {
                            Log.d(TAG, "onCharacteristicChanged: wrong credentials")
                            CredentialsResult.WrongPassword
                        }

                        RESPONSE_FAILURE -> {
                            Log.d(TAG, "onCharacteristicChanged: credentials failure")
                            CredentialsResult.Failure
                        }

                        else -> {
                            Log.d(TAG, "onCharacteristicChanged: unknown response")
                            CredentialsResult.Failure
                        }
                    }
                    pendingCredentialsRequest?.complete(result)
                }

                else -> {}
            }
        }
    }

    private fun createRequestString(ssid: String, password: String): String = "$ssid:$password"

    companion object {
        private const val TAG = "BleControlManager"
    }

}