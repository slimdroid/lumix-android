package com.slimdroid.lumix.ui.connection.search

import androidx.compose.runtime.Immutable
import com.slimdroid.lumix.core.model.LumixDevice

@Immutable
sealed interface SearchDeviceInNetworkUiState {

    val isRefreshEnable: Boolean

    data class Empty(
        override val isRefreshEnable: Boolean = false
    ) : SearchDeviceInNetworkUiState

    data class Content(
        override val isRefreshEnable: Boolean = true,
        val isProgress: Boolean,
        val deviceList: List<LumixDevice>
    ) : SearchDeviceInNetworkUiState

    companion object {
        fun default() = Content(
            deviceList = emptyList(),
            isProgress = false,
            isRefreshEnable = false,
        )

        fun empty() = Empty()
    }

}