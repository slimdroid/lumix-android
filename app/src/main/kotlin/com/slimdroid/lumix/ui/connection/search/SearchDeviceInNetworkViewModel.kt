package com.slimdroid.lumix.ui.connection.search

import android.util.Log
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
class SearchDeviceInNetworkViewModel @Inject constructor(
    private val repository: DeviceRepository,
    private val scanner: DeviceScanner
) : ViewModel() {

    private val isProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val scannedDevices: MutableStateFlow<List<LumixDevice>> = MutableStateFlow(emptyList())

    init {
        startScanner()
    }

    val uiState: StateFlow<SearchDeviceInNetworkUiState> = combine(
        repository.getDevices(),
        scannedDevices,
        isProgress
    ) { storedDevices, scannedDevices, progress ->
        SearchDeviceInNetworkUiState.Content(
            isProgress = progress,
            deviceList = scannedDevices
                .filterNot { scannedDevice ->
                    storedDevices.any { it.macAddress == scannedDevice.macAddress }
                }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchDeviceInNetworkUiState.default()
    )

    fun startScanner() {
        isProgress.value = true
        Log.d("SearchDeviceInNetworkViewModel", "startScanner")
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

    fun saveDevice(device: LumixDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveDevice(device)
        }
    }
}