package com.slimdroid.lumix.ui.device_list

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.slimdroid.lumix.core.model.LumixDevice

private val deviceList = listOf(
    LumixDevice(
        ipAddress = "192.168.1.100",
        macAddress = "LUMIX-AA-BB-CC",
        name = "LUMIX",
        firmware = "1.0.0",
        type = ""
    ),
    LumixDevice(
        ipAddress = "192.168.1.101",
        macAddress = "LUMIX-DD-EE-FF",
        name = "LUMIX",
        firmware = "1.0.1",
        type = ""
    ),
    LumixDevice(
        ipAddress = "192.168.1.102",
        macAddress = "LUMIX-GG-HH-II",
        name = "LUMIX",
        firmware = "1.0.2",
        type = ""
    ),
    LumixDevice(
        ipAddress = "192.168.1.103",
        macAddress = "LUMIX-JJ-KK-LL",
        name = "LUMIX",
        firmware = "1.0.3",
        type = ""
    ),
    LumixDevice(
        ipAddress = "192.168.1.104",
        macAddress = "LUMIX-MM-NN-OO",
        name = "LUMIX",
        firmware = "1.0.4",
        type = ""
    ),
    LumixDevice(
        ipAddress = "192.168.1.105",
        macAddress = "LUMIX-PP-QQ-RR",
        name = "LUMIX",
        firmware = "1.0.5",
        type = ""
    ),
    LumixDevice(
        ipAddress = "192.168.1.106",
        macAddress = "LUMIX-SS-TT-UU",
        name = "LUMIX",
        firmware = "1.0.6",
        type = ""
    ),
    LumixDevice(
        ipAddress = "192.168.1.107",
        macAddress = "LUMIX-VV-WW-XX",
        name = "LUMIX",
        firmware = "1.0.7",
        type = ""
    ),
    LumixDevice(
        ipAddress = "192.168.1.108",
        macAddress = "LUMIX-YY-ZZ-AA",
        name = "LUMIX",
        firmware = "1.0.8",
        type = ""
    ),
    LumixDevice(
        ipAddress = "192.168.1.109",
        macAddress = "LUMIX-BB-CC-DD",
        name = "LUMIX",
        firmware = "1.0.9",
        type = ""
    )
)

class DeviceListPreviewProvider : PreviewParameterProvider<DeviceListUiState> {
    override val values: Sequence<DeviceListUiState> = sequenceOf(
        DeviceListUiState.empty(),
        DeviceListUiState.Content(
            deviceList = deviceList.take(10),
            isRefreshEnable = true,
            isProgress = true
        )
    )
}
