package com.namma_shaale.inventory.presentation.assetlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma_shaale.inventory.data.local.entities.Asset
import com.namma_shaale.inventory.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssetListUiState(
    val assets: List<Asset> = emptyList(),
    val filteredAssets: List<Asset> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val locations: List<String> = emptyList(),
    val selectedLocation: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AssetListViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetListUiState())
    val uiState: StateFlow<AssetListUiState> = _uiState.asStateFlow()

    private val _allAssets = MutableStateFlow<List<Asset>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _selectedLocation = MutableStateFlow<String?>(null)

    init {
        loadAssets()
        observeFilters()
    }

    private fun loadAssets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                assetRepository.getAllAssets().collect { assetsList ->
                    _allAssets.value = assetsList
                    val cats = assetsList.map { it.category }.distinct().sorted()
                    val locs = assetsList.mapNotNull { it.location }.filter { it.isNotBlank() }.distinct().sorted()
                    _uiState.update { it.copy(
                        assets = assetsList,
                        categories = cats,
                        locations = locs,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun observeFilters() {
        viewModelScope.launch {
            combine(_allAssets, _searchQuery, _selectedCategory, _selectedLocation) { assets, query, category, location ->
                assets.filter { asset ->
                    val matchesQuery = asset.name.contains(query, ignoreCase = true) || 
                                     asset.serialNo?.contains(query, ignoreCase = true) == true ||
                                     asset.location?.contains(query, ignoreCase = true) == true
                    val matchesCategory = category == null || asset.category == category
                    val matchesLocation = location == null || asset.location == location
                    matchesQuery && matchesCategory && matchesLocation
                }
            }.collect { filtered ->
                _uiState.update { 
                    it.copy(
                        filteredAssets = filtered,
                        searchQuery = _searchQuery.value,
                        selectedCategory = _selectedCategory.value,
                        selectedLocation = _selectedLocation.value
                    ) 
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String?) {
        _selectedCategory.value = category
    }

    fun onLocationSelect(location: String?) {
        _selectedLocation.value = location
    }

    fun deleteAsset(asset: Asset) {
        viewModelScope.launch {
            try {
                assetRepository.deleteAsset(asset)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete: ${e.message}") }
            }
        }
    }

    fun updateAsset(asset: Asset) {
        viewModelScope.launch {
            try {
                assetRepository.updateAsset(asset)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to update: ${e.message}") }
            }
        }
    }
}
