package com.slimdroid.lumix.ui.connection.method

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentConnectionMethodBinding

class ConnectionMethodFragment : Fragment(R.layout.fragment_connection_method) {

    private var _binding: FragmentConnectionMethodBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView(view: View) {
        _binding = FragmentConnectionMethodBinding.bind(view)
    }

    private fun setListeners() {
        binding.apply {
            btnSearchDeviceInNetwork.setOnClickListener {
                findNavController().navigate(R.id.action_connectionMethodFragment_to_nav_connection_search_old)
            }
            btnConnectDeviceToNetwork.setOnClickListener {
                findNavController().navigate(R.id.action_connectionMethodFragment_to_nav_connection_add_new)
            }
            btnHotspot.setOnClickListener {
                findNavController().navigate(R.id.action_connectionMethodFragment_to_nav_connection_directly)
            }
            btnSmartConfig.setOnClickListener {
                findNavController().navigate(R.id.action_connectionMethodFragment_to_nav_connection_smart_config)
            }
        }
    }

}