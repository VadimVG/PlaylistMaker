package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel by viewModel<SettingsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(theme = checked)
        }

        settingsViewModel.isDarkThemeEnabled.observe(viewLifecycleOwner) {
            binding.themeSwitcher.isChecked = it
            applyTheme(it)
        }

        binding.settingsShare.setOnClickListener { settingsViewModel.shareApp() }
        binding.settingsSupport.setOnClickListener { settingsViewModel.openSupport() }
        binding.settingsDocs.setOnClickListener { settingsViewModel.openTerms() }

        settingsViewModel.actionCommand.observe(viewLifecycleOwner) { intent ->
            intent?.let { startActivity(it) }
            settingsViewModel.clearActionCommand()
        }

    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) MODE_NIGHT_YES else MODE_NIGHT_NO
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}