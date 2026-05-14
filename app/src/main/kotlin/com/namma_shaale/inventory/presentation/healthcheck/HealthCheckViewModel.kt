package com.namma_shaale.inventory.presentation.healthcheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma_shaale.inventory.data.local.entities.Asset
import com.namma_shaale.inventory.data.local.entities.HealthCheck
import com.namma_shaale.inventory.data.local.entities.RepairRequest
import com.namma_shaale.inventory.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HealthCheckUiState(
    val assets: List<Asset> = emptyList(),
    val checkedAssetIds: Set<String> = emptySet(),
    val currentIndex: Int = 0,
    val updatedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val newYellowCount: Int = 0,
    val newRedCount: Int = 0,
    val selectedCategory: String? = null,
    val selectedLocation: String? = null,
    val searchQuery: String = "",
    val categories: List<String> = emptyList(),
    val locations: List<String> = emptyList(),
    val assetNotes: Map<String, String> = emptyMap(),
    val errorMessage: String? = null
)

@HiltViewModel
class HealthCheckViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthCheckUiState(isLoading = true))
    val uiState: StateFlow<HealthCheckUiState> = _uiState.asStateFlow()

    init {
        loadAssets()
    }

    private fun loadAssets() {
        viewModelScope.launch {
            try {
                assetRepository.getAllAssets().collect { assets ->
                    val categories = assets.map { it.category }.distinct().sorted()
                    val locations = assets.mapNotNull { it.location }.distinct().sorted()
                    _uiState.update {
                        it.copy(
                            assets = assets,
                            categories = categories,
                            locations = locations,
                            totalCount = assets.size,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load assets: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateAssetStatus(assetId: String, newStatus: String) {
        val note = _uiState.value.assetNotes[assetId] ?: ""
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val asset = currentState.assets.find { it.assetId == assetId } ?: return@launch

                assetRepository.updateAssetStatus(assetId, newStatus)

                val healthCheck = HealthCheck(
                    assetId = assetId,
                    status = newStatus,
                    checkedDate = System.currentTimeMillis(),
                    notes = note.ifBlank { null }
                )
                assetRepository.insertHealthCheck(healthCheck)

                if (newStatus in listOf("YELLOW", "RED")) {
                    val existingRequest = assetRepository.getRepairRequestByAssetId(assetId)
                    if (existingRequest == null) {
                        val repairRequest = RepairRequest(
                            assetId = assetId,
                            raisedDate = System.currentTimeMillis(),
                            status = "PENDING",
                            priority = if (newStatus == "RED") "HIGH" else "MEDIUM"
                        )
                        assetRepository.insertRepairRequest(repairRequest)
                    } else if (newStatus == "RED" && existingRequest.priority == "MEDIUM" && existingRequest.status == "PENDING") {
                        // Upgrade priority if it's now broken
                        val updatedRequest = existingRequest.copy(priority = "HIGH")
                        assetRepository.updateRepairRequest(updatedRequest)
                    }
                } else if (newStatus == "GREEN") {
                    val existingRequest = assetRepository.getRepairRequestByAssetId(assetId)
                    if (existingRequest != null) {
                        val resolvedRequest = existingRequest.copy(
                            status = "RESOLVED",
                            resolvedDate = System.currentTimeMillis()
                        )
                        assetRepository.updateRepairRequest(resolvedRequest)
                    }
                }

                val isNewCheck = !currentState.checkedAssetIds.contains(assetId)
                val newCheckedIds = currentState.checkedAssetIds + assetId
                
                val newYellow = if (isNewCheck && newStatus == "YELLOW") 1 else 0
                val newRed = if (isNewCheck && newStatus == "RED") 1 else 0

                _uiState.update {
                    it.copy(
                        checkedAssetIds = newCheckedIds,
                        updatedCount = newCheckedIds.size,
                        currentIndex = it.currentIndex + if (isNewCheck) 1 else 0,
                        newYellowCount = it.newYellowCount + newYellow,
                        newRedCount = it.newRedCount + newRed,
                        isCompleted = newCheckedIds.size >= it.totalCount
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update status: ${e.message}")
                }
            }
        }
    }

    fun updateSelectedCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun updateSelectedLocation(location: String?) {
        _uiState.update { it.copy(selectedLocation = location) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateAssetNote(assetId: String, note: String) {
        _uiState.update { state ->
            val newNotes = state.assetNotes.toMutableMap()
            newNotes[assetId] = note
            state.copy(assetNotes = newNotes)
        }
    }

    fun markAllRemainingAsWorking() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val filteredAssets = currentState.assets.filter { asset ->
                    val matchesCategory = currentState.selectedCategory == null || asset.category == currentState.selectedCategory
                    val matchesLocation = currentState.selectedLocation == null || asset.location == currentState.selectedLocation
                    val matchesSearch = currentState.searchQuery.isBlank() || 
                        asset.name.contains(currentState.searchQuery, ignoreCase = true) ||
                        (asset.serialNo?.contains(currentState.searchQuery, ignoreCase = true) ?: false)
                    
                    matchesCategory && matchesLocation && matchesSearch
                }
                
                val uncheckedAssets = filteredAssets.filter { !currentState.checkedAssetIds.contains(it.assetId) }
                
                uncheckedAssets.forEach { asset ->
                    assetRepository.updateAssetStatus(asset.assetId, "GREEN")
                    val healthCheck = HealthCheck(
                        assetId = asset.assetId,
                        status = "GREEN",
                        checkedDate = System.currentTimeMillis()
                    )
                    assetRepository.insertHealthCheck(healthCheck)
                }

                val newCheckedIds = currentState.checkedAssetIds + uncheckedAssets.map { it.assetId }.toSet()
                
                _uiState.update { state ->
                    state.copy(
                        checkedAssetIds = newCheckedIds,
                        updatedCount = newCheckedIds.size,
                        isCompleted = newCheckedIds.size >= state.totalCount
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Bulk update failed: ${e.message}") }
            }
        }
    }

    fun deleteAsset(asset: Asset) {
        viewModelScope.launch {
            try {
                assetRepository.deleteAsset(asset)
                loadAssets()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to delete asset: ${e.message}")
                }
            }
        }
    }

    fun resetHealthCheck() {
        _uiState.update {
            it.copy(
                checkedAssetIds = emptySet(),
                currentIndex = 0,
                updatedCount = 0,
                newYellowCount = 0,
                newRedCount = 0,
                isCompleted = false
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
