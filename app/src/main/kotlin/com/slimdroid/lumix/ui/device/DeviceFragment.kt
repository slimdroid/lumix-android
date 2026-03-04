package com.slimdroid.lumix.ui.device

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentDeviceBinding

class DeviceFragment : Fragment(R.layout.fragment_device) {

    private var _binding: FragmentDeviceBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        initNavController()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView(view: View) {
        _binding = FragmentDeviceBinding.bind(view)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun initNavController() {
        val localNavHostFragment =
            childFragmentManager.findFragmentById(R.id.local_nav_host_fragment) as NavHostFragment
        navController = localNavHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.navigation.nav_device_control,
                R.navigation.nav_device_effects,
                R.navigation.nav_device_settings
            )
        )

        binding.toolbar.apply {
            setupWithNavController(navController, appBarConfiguration)
            setNavigationOnClickListener {
                if (!navController.navigateUp())
                    findNavController().popBackStack()
            }
        }

        binding.requireNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.deviceControlFragment) {
                binding.toolbar.apply {
                    setNavigationIcon(R.drawable.ic_24_close)
                    navigationContentDescription = getString(R.string.close)
                }
            }
        }
    }

    private val FragmentDeviceBinding.requireNavigationView: NavigationBarView
        get() = navigationView as NavigationBarView

}