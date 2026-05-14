package com.namma_shaale.inventory.presentation.assethistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma_shaale.inventory.data.local.entities.Asset
import com.namma_shaale.inventory.data.local.entities.HealthCheck
import com.namma_shaale.inventory.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssetHistoryUiState(
    val asset: Asset? = null,
    val history: List<HealthCheck> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class AssetHistoryViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetHistoryUiState())
    val uiState: StateFlow<AssetHistoryUiState> = _uiState.asStateFlow()

    fun loadAssetHistory(assetId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val asset = assetRepository.getAssetById(assetId)
            _uiState.update { it.copy(asset = asset) }
            
            assetRepository.getHealthCheckHistory(assetId).collect { history ->
                _uiState.update { it.copy(history = history, isLoading = false) }
            }
        }
    }
}
