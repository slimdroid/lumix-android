package com.slimdroid.lumix.ui.device_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slimdroid.lumix.core.data.repository.DeviceRepository
import com.slimdroid.lumix.core.model.LumixDevice
import com.slimdroid.lumix.core.scanner.DeviceScanner
import com.slimdroid.lumix.core.scanner.toExternalModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    repository: DeviceRepository,
    private val scanner: DeviceScanner
) : ViewModel() {

    private val isProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val scannedDevices: MutableStateFlow<List<LumixDevice>> = MutableStateFlow(emptyList())

    init {
        startScanner()
    }

    val uiState: StateFlow<DeviceListUiState> = combine(
        repository.getDevices(),
        scannedDevices,
        isProgress
    ) { storedDevices, scannedDevices, progress ->
        if (storedDevices.isEmpty()) {
            stopScanner()
            return@combine DeviceListUiState.Empty
        } else {
            DeviceListUiState.Content(
                isProgress = progress,
                deviceList = storedDevices.map { device ->
                    val scannedDevice = scannedDevices
                        .find { it.macAddress == device.macAddress }
                        ?.also {
                            if (it != device) {
                                viewModelScope.launch {
                                    repository.updateDevice(it)
                                }
                            }
                        }
                    device.copy(online = scannedDevice != null)
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DeviceListUiState.Content.default()
    )

    fun startScanner() {
        isProgress.value = false
        viewModelScope.launch(Dispatchers.IO) {
            scanner.startScanFlow()
                .onCompletion {
                    isProgress.value = false
                }
                .collect { devices ->
                    scannedDevices.value = devices.map { it.toExternalModel() }
                }
        }
    }

    fun stopScanner() {
        isProgress.value = false
        scanner.stopScan()
    }

}