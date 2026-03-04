package com.slimdroid.lumix.ui.device_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slimdroid.lumix.scanner.DeviceScanner
import com.slimdroid.lumix.scanner.DeviceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeviceListViewModel : ViewModel() {

    private val scanner = DeviceScanner(DeviceType.LUMIX)
    private val _uiState = MutableStateFlow(DeviceListUiState(btnAction = { startScan() }))
    val uiState: StateFlow<DeviceListUiState> = _uiState

    private fun startScan() {
        _uiState.update { uiState ->
            uiState.copy(
                isProgressShow = true,
                btnAction = { stopScan() }
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            scanner.startScanFlow()
                .onCompletion {
                    _uiState.update { uiState ->
                        uiState.copy(
                            isProgressShow = false,
                            btnAction = { startScan() })
                    }
                }
                .collect { devices ->
                    _uiState.update { uiState ->
                        uiState.copy(deviceList = devices)
                    }
                }
        }
    }

    private fun stopScan() {
        _uiState.update { uiState ->
            uiState.copy(
                isProgressShow = false,
                btnAction = { startScan() }
            )
        }
        scanner.stopScan()
    }

}