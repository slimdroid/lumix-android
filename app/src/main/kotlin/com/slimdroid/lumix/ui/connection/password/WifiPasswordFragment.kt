package com.slimdroid.lumix.ui.connection.password

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentWifiPasswordBinding
import com.slimdroid.lumix.ui.connection.bluetooth.BluetoothConnectionFragment

class WifiPasswordFragment : Fragment(R.layout.fragment_wifi_password) {

    private val viewModel: WifiPasswordViewModel by viewModels {
        WifiPasswordViewModelFactory()
    }

    private var _binding: FragmentWifiPasswordBinding? = null
    private val binding get() = _binding!!


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
        _binding = FragmentWifiPasswordBinding.bind(view)
    }

    private fun setObservables() {
        viewModel.apply {
            ssidLiveData.observe(viewLifecycleOwner) {
                binding.textMessage.text = it
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnAnotherWifi.setOnClickListener {
                findNavController().navigate(R.id.action_wifiPasswordFragment_to_wifiScanningFragment)
            }
            btnWifiSettings.setOnClickListener {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            btnSubmitPassword.setOnClickListener {
                setFragmentResult(
                    BluetoothConnectionFragment.REQUEST_KEY_CREDENTIALS,
                    BluetoothConnectionFragment.getBundleForResult(
                        binding.textMessage.text.toString(),
                        etPassword.text.toString()
                    )
                )
                findNavController().popBackStack()
            }
        }
    }

}