package com.slimdroid.lumix.ui.device_list

import androidx.compose.runtime.Immutable
import com.slimdroid.lumix.core.model.LumixDevice

@Immutable
sealed interface DeviceListUiState {

    val isRefreshEnable: Boolean

    data class Empty(
        override val isRefreshEnable: Boolean = false
    ) : DeviceListUiState

    data class Content(
        override val isRefreshEnable: Boolean = true,
        val isProgress: Boolean,
        val deviceList: List<LumixDevice>
    ) : DeviceListUiState

    companion object {
        fun default() = Content(
            deviceList = emptyList(),
            isProgress = false,
            isRefreshEnable = false,
        )

        fun empty() = Empty()
    }

}