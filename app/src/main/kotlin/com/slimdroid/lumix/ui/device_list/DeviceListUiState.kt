package com.slimdroid.lumix.ui.device_list

import com.slimdroid.lumix.core.model.LumixDevice

sealed interface DeviceListUiState {

    data object Empty : DeviceListUiState

    data class Content(
        val isProgress: Boolean,
        val deviceList: List<LumixDevice>
    ) : DeviceListUiState {

        companion object {
            fun default() = Content(
                isProgress = false,
                deviceList = emptyList()
            )
        }
    }
}