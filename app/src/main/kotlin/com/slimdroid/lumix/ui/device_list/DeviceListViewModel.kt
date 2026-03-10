package com.slimdroid.lumix.ui.device_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slimdroid.lumix.core.data.repository.DeviceRepository
import com.slimdroid.lumix.scanner.DeviceScanner
import com.slimdroid.lumix.scanner.DeviceScannerImpl
import com.slimdroid.lumix.scanner.DeviceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val repository: DeviceRepository,
    private val scanner: DeviceScanner = DeviceScannerImpl(DeviceType.LUMIX)
) : ViewModel() {

    private val devices = repository.getDevices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val scannedDevices = scanner.startScanFlow()

    private val resultFlow = combine(
        devices,
        scannedDevices
    ) { devices, scannedDevices ->
        val resultDevices = devices
        scannedDevices.forEach { scDevice ->
            val r = devices.any { it.id == scDevice.deviceId }
            if (r) {

            }
        }


        ""
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ""
    )

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