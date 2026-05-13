package com.namma_shaale.inventory.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma_shaale.inventory.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val totalAssets: Int = 0,
    val greenCount: Int = 0,
    val yellowCount: Int = 0,
    val redCount: Int = 0,
    val pendingRepairs: Int = 0,
    val healthPercentage: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val total = assetRepository.getTotalAssetCount()
                val green = assetRepository.getWorkingAssetCount()
                val yellow = assetRepository.getNeedsRepairCount()
                val red = assetRepository.getBrokenLostCount()
                val pending = assetRepository.getPendingRepairCount()

                val healthPercent = if (total > 0) (green * 100) / total else 0

                _uiState.update {
                    it.copy(
                        totalAssets = total,
                        greenCount = green,
                        yellowCount = yellow,
                        redCount = red,
                        pendingRepairs = pending,
                        healthPercentage = healthPercent,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load dashboard data: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
