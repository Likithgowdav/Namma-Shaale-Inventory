package com.namma_shaale.inventory.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma_shaale.inventory.data.local.entities.SchoolSettings
import com.namma_shaale.inventory.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    val schoolName: StateFlow<String> = schoolRepository.getSettings()
        .map { it?.schoolName ?: "Namma Shaale" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Namma Shaale")

    val schoolAddress: StateFlow<String> = schoolRepository.getSettings()
        .map { it?.schoolAddress ?: "Address not set" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Address not set")

    val schoolCode: StateFlow<String> = schoolRepository.getSettings()
        .map { it?.schoolCode ?: "DISE-CODE-NOT-SET" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "DISE-CODE-NOT-SET")

    val languageCode: StateFlow<String> = schoolRepository.getSettings()
        .map { it?.languageCode ?: "en" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")
        
    val isDarkMode: StateFlow<Boolean?> = schoolRepository.getSettings()
        .map { it?.isDarkMode }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateSchoolName(name: String) {
        viewModelScope.launch {
            schoolRepository.updateSchoolName(name)
        }
    }

    fun updateSchoolAddress(address: String) {
        viewModelScope.launch {
            schoolRepository.updateSchoolAddress(address)
        }
    }

    fun updateSchoolCode(code: String) {
        viewModelScope.launch {
            schoolRepository.updateSchoolCode(code)
        }
    }

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            schoolRepository.updateLanguage(lang)
        }
    }

    fun resetDatabase() {
        viewModelScope.launch {
            schoolRepository.clearAllData()
        }
    }

    fun updateDarkMode(isDark: Boolean?) {
        viewModelScope.launch {
            schoolRepository.updateDarkMode(isDark)
        }
    }
}
