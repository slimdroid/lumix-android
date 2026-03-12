package com.slimdroid.lumix.ui.device_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.slimdroid.lumix.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceListFragment : Fragment() {

    private val viewModel: DeviceListViewModel by viewModels()

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val state by viewModel.uiState.collectAsState()
            MaterialExpressiveTheme(
                colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            ) {
                DeviceListScreen(
                    state = state,
                    onDeviceClick = { device ->
                        val ipAddress = device.ipAddress
                        val args = Bundle().apply {
                            putString("ipAddress", ipAddress)
                        }
                        viewModel.stopScanner()
                        findNavController().navigate(
                            R.id.action_deviceListFragment_to_deviceFragment,
                            args
                        )
                    },
                    onStartScannerClick = {
                        viewModel.startScanner()
                    },
                    onAddNewDeviceClick = {
                        findNavController().navigate(R.id.action_deviceListFragment_to_connectionFragment2)
                    }
                )
            }
        }
    }

}