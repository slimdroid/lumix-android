package com.slimdroid.lumix.ui.connection.password

import android.net.wifi.WifiManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WifiPasswordViewModel(private val wifiManager: WifiManager) : ViewModel() {

    private val _ssidLiveData = MutableLiveData<String>()
    val ssidLiveData: LiveData<String> = _ssidLiveData

    init {
        getCurrentSSID()
    }

    /**
     * Помните, что каждый раз, когда пользователь отключается или подключается к новому SSID
     * или любому изменению состояния Wi-Fi, вам необходимо инициализировать WifiInfo,
     * т.е. wifiInfo = wifiManager.getConnectionInfo();
     */
    // https://github.com/EspressifApp/lib-esptouch-v2-android/blob/main/esptouch-v2/src/main/java/com/espressif/iot/esptouch2/provision/TouchNetUtil.java
    private fun getCurrentSSID() {
        wifiManager.connectionInfo?.let { wifiInfo ->
            val ssid = wifiInfo.ssid
            if (ssid != WifiManager.UNKNOWN_SSID) {
//                _ssidLiveData.value = "SSID: $ssid, BSSID: ${it.bssid}"
                _ssidLiveData.value = "SSID: ${ssid.replace("\"", "")}"
            } else {
                _ssidLiveData.value = "unknown ssid"
            }
        } ?: run {
            _ssidLiveData.value = "No Connection"
        }

    }

}