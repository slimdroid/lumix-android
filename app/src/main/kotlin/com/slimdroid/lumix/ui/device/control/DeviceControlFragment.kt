package com.slimdroid.lumix.ui.device.control

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentDeviceControlBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceControlFragment : Fragment(R.layout.fragment_device_control) {

    private val viewModel: DeviceControlViewModel by viewModels()

    private var _binding: FragmentDeviceControlBinding? = null
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
        _binding = FragmentDeviceControlBinding.bind(view)
    }

    private fun setListeners() {
        binding.apply {
            btnNext.setOnClickListener {
                viewModel.setNextEffect()
            }
            btnPrevious.setOnClickListener {
                viewModel.setPreviousEffect()
            }
            btnLedCount.setOnClickListener {
                val ledCount = editTextLedCount.text.toString()
                viewModel.setLedCount(ledCount)
            }
            switchPower.setOnCheckedChangeListener { _, isChecked ->
                viewModel.powerToggle(isChecked)
            }
            sliderBrightness.addOnChangeListener { _, value, _ ->
                viewModel.setBrightness(value.toInt())
            }
        }
    }

    private fun setObservables() {
        viewModel.apply {

        }
    }

}