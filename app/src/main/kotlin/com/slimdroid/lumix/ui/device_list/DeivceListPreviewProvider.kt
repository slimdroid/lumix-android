package com.slimdroid.lumix.ui.device_list

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.slimdroid.lumix.scanner.Device
import com.slimdroid.lumix.scanner.DeviceType

private val deviceList = listOf(
    Device(
        deviceIp = "192.168.1.100",
        deviceId = "LUMIX-AA-BB-CC",
        name = "LUMIX",
        appVersion = "1.0.0",
        type = DeviceType.LUMIX
    ),
    Device(
        deviceIp = "192.168.1.101",
        deviceId = "LUMIX-DD-EE-FF",
        name = "LUMIX",
        appVersion = "1.0.1",
        type = DeviceType.LUMIX
    ),
    Device(
        deviceIp = "192.168.1.102",
        deviceId = "LUMIX-GG-HH-II",
        name = "LUMIX",
        appVersion = "1.0.2",
        type = DeviceType.LUMIX
    ),
    Device(
        deviceIp = "192.168.1.103",
        deviceId = "LUMIX-JJ-KK-LL",
        name = "LUMIX",
        appVersion = "1.0.3",
        type = DeviceType.LUMIX
    ),
    Device(
        deviceIp = "192.168.1.104",
        deviceId = "LUMIX-MM-NN-OO",
        name = "LUMIX",
        appVersion = "1.0.4",
        type = DeviceType.LUMIX
    ),
    Device(
        deviceIp = "192.168.1.105",
        deviceId = "LUMIX-PP-QQ-RR",
        name = "LUMIX",
        appVersion = "1.0.5",
        type = DeviceType.LUMIX
    ),
    Device(
        deviceIp = "192.168.1.106",
        deviceId = "LUMIX-SS-TT-UU",
        name = "LUMIX",
        appVersion = "1.0.6",
        type = DeviceType.LUMIX
    ),
    Device(
        deviceIp = "192.168.1.107",
        deviceId = "LUMIX-VV-WW-XX",
        name = "LUMIX",
        appVersion = "1.0.7",
        type = DeviceType.LUMIX
    ),
    Device(
        deviceIp = "192.168.1.108",
        deviceId = "LUMIX-YY-ZZ-AA",
        name = "LUMIX",
        appVersion = "1.0.8",
        type = DeviceType.LUMIX
    ),
    Device(
        deviceIp = "192.168.1.109",
        deviceId = "LUMIX-BB-CC-DD",
        name = "LUMIX",
        appVersion = "1.0.9",
        type = DeviceType.LUMIX
    )
)

class DeviceListPreviewProvider : PreviewParameterProvider<DeviceListUiState> {
    override val values: Sequence<DeviceListUiState> = sequenceOf(
        DeviceListUiState(
            deviceList = deviceList.take(10),
            isProgressShow = true
        )
    )
}
