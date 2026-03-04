package com.slimdroid.lumix.dependency

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.wifi.WifiManager
import com.slimdroid.lumix.App
import com.slimdroid.lumix.data.BleControlManager

object Dependencies {

    val bluetoothAdapter: BluetoothAdapter? by lazy { createBluetoothAdapter() }

    val bleControlManager: BleControlManager by lazy { createBleControlManager() }

    val wifiManager: WifiManager by lazy { createWifiManager() }


    private fun createBluetoothAdapter(): BluetoothAdapter? =
        (App.instance.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private fun createBleControlManager(): BleControlManager =
        BleControlManager(App.instance, bluetoothAdapter)

    private fun createWifiManager(): WifiManager =
        App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
}