package com.slimdroid.lumix.ui.connection.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.slimdroid.lumix.theme.LumixTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchDeviceInNetworkFragment : Fragment() {

    private val viewModel: SearchDeviceInNetworkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val state by viewModel.uiState.collectAsState()
            LumixTheme {
                SearchDeviceInNetworkScreen(
                    state = state,
                    onDeviceSelected = { device ->
                        viewModel.saveDevice(device)
                    },
                    onRefresh = {
                        viewModel.startScanner()
                    }
                )
            }
        }
    }

}