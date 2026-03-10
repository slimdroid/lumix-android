package com.slimdroid.lumix.ui.connection.smart_config

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentSmartConfigBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmartConfigFragment : Fragment(R.layout.fragment_smart_config) {

    private val viewModel: SmartConfigViewModel by viewModels()

    private var _binding: FragmentSmartConfigBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        initToolbarMenu()
        setListeners()
        setObservables()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView(view: View) {
        _binding = FragmentSmartConfigBinding.bind(view)
    }

    private fun initToolbarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_test, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_test -> {
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setListeners() {
        binding.apply {

        }
    }

    private fun setObservables() {
        viewModel.apply {

        }
    }

}