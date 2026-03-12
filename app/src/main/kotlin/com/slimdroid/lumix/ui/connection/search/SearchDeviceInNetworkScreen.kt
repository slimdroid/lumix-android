package com.slimdroid.lumix.ui.connection.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slimdroid.lumix.core.model.LumixDevice
import com.slimdroid.lumix.theme.LumixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDeviceInNetworkScreen(
    state: SearchDeviceInNetworkUiState,
    onDeviceSelected: (LumixDevice) -> Unit,
    onRefresh: () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = (state as? SearchDeviceInNetworkUiState.Content)?.isProgress ?: false

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                isRefreshing = isRefreshing,
                state = pullToRefreshState,
                enabled = state.isRefreshEnable,
                onRefresh = onRefresh
            ),
        topBar = {
            TopAppBar(
                title = { Text(text = "Search devices") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                is SearchDeviceInNetworkUiState.Content -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.deviceList, key = { it.macAddress }) { device ->
                            DeviceItem(
                                device = device,
                                onClick = { onDeviceSelected(device) }
                            )
                        }
                    }
                    if (state.isProgress) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                is SearchDeviceInNetworkUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No devices found",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Preview
@Composable
private fun SearchDeviceInNetworkScreenPreview() {
    LumixTheme {
        Surface {
            SearchDeviceInNetworkScreen(
                state = SearchDeviceInNetworkUiState.Content(
                    isProgress = true,
                    deviceList = listOf(
                        LumixDevice(
                            macAddress = "00:11:22:33:44:55",
                            ipAddress = "192.168.1.10",
                            name = "Living Room",
                            type = "LUMIX",
                            firmware = "1.0.0",
                            online = true
                        ),
                        LumixDevice(
                            macAddress = "AA:BB:CC:DD:EE:FF",
                            ipAddress = "192.168.1.11",
                            name = "Kitchen",
                            type = "LUMIX",
                            firmware = "1.0.0",
                            online = false
                        )
                    )
                ),
                onDeviceSelected = {},
                onRefresh = {}
            )
        }
    }
}

@Preview
@Composable
private fun SearchDeviceInNetworkScreenEmptyPreview() {
    LumixTheme {
        Surface {
            SearchDeviceInNetworkScreen(
                state = SearchDeviceInNetworkUiState.Empty(),
                onDeviceSelected = {},
                onRefresh = {}
            )
        }
    }
}

@Composable
private fun DeviceItem(
    device: LumixDevice,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        ListItem(
            headlineContent = { Text(text = device.macAddress) },
            supportingContent = { Text(text = device.ipAddress) }
        )
    }
}
