package com.slimdroid.lumix.ui.connection.scanner

import android.Manifest
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentDeviceScannerBinding
import com.slimdroid.lumix.ui.connection.bluetooth.BluetoothConnectionFragment
import com.slimdroid.lumix.utils.PermissionStatus
import com.slimdroid.lumix.utils.ResUtils
import com.slimdroid.lumix.utils.requestPermissionLauncher

class BluetoothScannerFragment : Fragment(R.layout.fragment_device_scanner) {

    private val viewModel: DeviceScannerViewModel by viewModels {
        BluetoothScannerViewModelFactory()
    }

    private var _binding: FragmentDeviceScannerBinding? = null
    private val binding get() = _binding!!

    private val deviceScanAdapter = BluetoothScannerAdapter()

    private val bluetoothPermissionsLauncher by requestPermissionLauncher { status ->
        when (status) {
            PermissionStatus.Granted -> {
                activateBluetooth()
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
                showBluetoothPermissionDeniedMessage()
            }
        }
    }

    private val requestEnableBluetooth = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
            showRationalBluetoothMessage()
        } else if (result.resultCode == AppCompatActivity.RESULT_OK) {
            activateBluetooth()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        setListeners()
        setObservables()

        checkBluetoothIsAvailable()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView(view: View) {
        _binding = FragmentDeviceScannerBinding.bind(view)
        binding.recyclerView.apply {
            adapter = deviceScanAdapter
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
            deviceList.observe(viewLifecycleOwner) {
                deviceScanAdapter.setItems(it)
            }
            scannerWarning.observe(viewLifecycleOwner) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {}
                    .show()
            }
            startScanFlag.observe(viewLifecycleOwner) {
                startScan()
            }
            message.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
            scanProgress.observe(viewLifecycleOwner) {
                if (it) {
                    binding.progress.show()
                    binding.btnStopScanning.visibility = View.VISIBLE
                } else {
                    binding.progress.hide()
                    binding.btnStopScanning.visibility = View.GONE
                }
            }
        }
    }

    private fun setListeners() {
        deviceScanAdapter.setOnItemClickListener {
            viewModel.stopScanning()
            findNavController().navigate(
                R.id.action_deviceScannerFragment_to_bluetoothConnectionFragment,
                BluetoothConnectionFragment.getBundle(it.address)
            )
        }
        binding.apply {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                startScan()
            }
            btnStopScanning.setOnClickListener {
                viewModel.stopScanning()
            }
        }
    }

    private fun checkBluetoothIsAvailable() {
        if (!requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.device_scanner_bluetooth_dialog_title)
                .setMessage(R.string.device_scanner_bluetooth_dialog_message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requireActivity().finish()
                }
                .show()
    }

    private fun activateBluetooth() {
        val bluetoothAdapter =
            (requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        when (bluetoothAdapter.state) {
            STATE_ON -> {
                viewModel.startScanning()
            }
            STATE_TURNING_ON -> {
                activateBluetooth()
            }
            STATE_TURNING_OFF, STATE_OFF -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        bluetoothAdapter.enable()
                        activateBluetooth()
                    } else {
                        requestEnableBluetooth.launch(Intent(ACTION_REQUEST_ENABLE))
                    }
                } else {
                    bluetoothAdapter.enable()
                    activateBluetooth()
                }
            }
        }
    }

    private fun startScan() {
        checkPermissions()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            Toast.makeText(requireContext(), Build.VERSION.SDK_INT.toString(), Toast.LENGTH_LONG)
                .show()
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION //or Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showBluetoothLocationPermissionMessage()
            } else {
                activateBluetooth()
            }
        } else {
            bluetoothPermissionsLauncher.launch(getListPermissions())
        }
    }

    private fun showBluetoothLocationPermissionMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.device_scanner_bluetooth_permission_dialog_title)
            .setMessage(R.string.device_scanner_bluetooth_permission_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                bluetoothPermissionsLauncher.launch(getListPermissions())
            }
            .show()
    }

    private fun showBluetoothPermissionDeniedMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.device_scanner_bluetooth_permission_denied_dialog_title)
            .setMessage(R.string.device_scanner_bluetooth_permission_denied_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                bluetoothPermissionsLauncher.launch(getListPermissions())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showRationalBluetoothMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.device_scanner_bluetooth_switch_on_denied_dialog_title)
            .setMessage(R.string.device_scanner_bluetooth_switch_on_denied_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                activateBluetooth()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun getListPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.R -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            else -> arrayOf()
        }
    }

}