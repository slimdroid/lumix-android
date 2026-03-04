package com.slimdroid.lumix.ui.connection.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentSearchDeviceInNetworkBinding

class SearchDeviceInNetworkFragment : Fragment(R.layout.fragment_search_device_in_network) {

    private val viewModel: SearchDeviceInNetworkViewModel by viewModels()

    private var _binding: FragmentSearchDeviceInNetworkBinding? = null
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
        _binding = FragmentSearchDeviceInNetworkBinding.bind(view)
    }

    private fun setObservables() {
        viewModel.apply {

        }
    }

    private fun setListeners() {
        binding.apply {

        }
    }

}