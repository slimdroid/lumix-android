package com.slimdroid.lumix.ui.connection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentConnectionBinding

class ConnectionFragment : Fragment(R.layout.fragment_connection) {

    private var _binding: FragmentConnectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        initNavController()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView(view: View) {
        _binding = FragmentConnectionBinding.bind(view)
    }

    private fun initNavController() {
        val localNavHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_connection_fragment) as NavHostFragment
        navController = localNavHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.navigation.nav_connection_add_new,
                R.navigation.nav_connection_directly,
                R.navigation.nav_connection_search_old
            )
        )

        binding.toolbar.apply {
            setupWithNavController(navController, appBarConfiguration)
            setNavigationOnClickListener {
                if (!navController.navigateUp())
                    findNavController().popBackStack()
            }
        }

    }
}