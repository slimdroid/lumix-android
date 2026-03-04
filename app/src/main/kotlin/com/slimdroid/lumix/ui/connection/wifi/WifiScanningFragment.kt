package com.slimdroid.lumix.ui.connection.wifi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentWifiScanningBinding
import com.slimdroid.lumix.utils.PermissionStatus
import com.slimdroid.lumix.utils.ResUtils
import com.slimdroid.lumix.utils.requestPermissionLauncher

class WifiScanningFragment : Fragment(R.layout.fragment_wifi_scanning) {

    private val viewModel: WifiScanningViewModel by viewModels { WifiScanningViewModelFactory() }

    private var _binding: FragmentWifiScanningBinding? = null
    private val binding get() = _binding!!

    private val wifiScanningAdapter = WifiScanningAdapter()

    private val locationPermissionsLauncher by requestPermissionLauncher { status ->
        when (status) {
            PermissionStatus.Granted -> {
                viewModel.startScanning()
            }
            PermissionStatus.Denied -> {
                Snackbar.make(
                    binding.root,
                    R.string.device_scanner_bluetooth_permission_denied_dialog_message,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.device_scanner_settings) {
                    startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", requireActivity().packageName, null)
                        }
                    )
                }.show()
            }
            PermissionStatus.ShowRationale -> {
                showLocationPermissionDeniedMessage()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        setListeners()
        setObservables()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView(view: View) {
        _binding = FragmentWifiScanningBinding.bind(view)

        binding.recyclerView.apply {
            adapter = wifiScanningAdapter
            addItemDecoration(
                MaterialDividerItemDecoration(requireContext(), RecyclerView.VERTICAL).also {
                    it.dividerInsetStart = ResUtils.dpToPixel(32).toInt()
                    it.dividerInsetEnd = ResUtils.dpToPixel(32).toInt()
                    it.isLastItemDecorated = false
                })
        }
    }

    private fun setObservables() {
        viewModel.apply {
            wifiList.observe(viewLifecycleOwner) {
                wifiScanningAdapter.setItems(it)
            }
            startScanFlag.observe(viewLifecycleOwner) {
                startScan()
            }
            scannerWarning.observe(viewLifecycleOwner) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, null)
                    .show()
            }
            scanProgress.observe(viewLifecycleOwner) {
                if (it) binding.progress.show() else binding.progress.hide()
            }
        }
    }

    private fun setListeners() {
        binding.apply {

        }
    }

    private fun startScan() {
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showLocationPermissionMessage()
        } else {
            viewModel.startScanning()
        }
    }

    private fun showLocationPermissionMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.wifi_scanning_location_permission_dialog_title)
            .setMessage(R.string.wifi_scanning_location_permission_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                locationPermissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
            .show()
    }

    private fun showLocationPermissionDeniedMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.wifi_scanning_location_permission_denied_dialog_title)
            .setMessage(R.string.wifi_scanning_location_permission_denied_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                locationPermissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

}