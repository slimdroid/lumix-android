package com.slimdroid.lumix.ui.device_list

import com.slimdroid.lumix.scanner.Device

data class DeviceListUiState(
    val deviceList: List<Device> = emptyList(),
    val btnAction: () -> Unit = {},
    val isProgressShow: Boolean = false
)