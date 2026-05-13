package com.namma_shaale.inventory.presentation.asset

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma_shaale.inventory.data.local.entities.Asset
import com.namma_shaale.inventory.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AddAssetUiState(
    val assetName: String = "",
    val category: String = "",
    val serialNo: String = "",
    val purchaseYearStr: String = "",
    val quantityStr: String = "",
    val location: String = "",
    val photoUri: Uri? = null,
    val photoPath: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isFormValid: Boolean = false,
    val createIndividually: Boolean = false
)

@HiltViewModel
class AddAssetViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAssetUiState())
    val uiState: StateFlow<AddAssetUiState> = _uiState.asStateFlow()

    fun updateAssetName(name: String) {
        _uiState.update { it.copy(assetName = name) }
        validateForm()
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(category = category) }
        validateForm()
    }

    fun updateSerialNo(serialNo: String) {
        _uiState.update { it.copy(serialNo = serialNo) }
    }

    fun updatePurchaseYear(yearStr: String) {
        _uiState.update { it.copy(purchaseYearStr = yearStr) }
        validateForm()
    }

    fun updateQuantity(qtyStr: String) {
        _uiState.update { it.copy(quantityStr = qtyStr) }
        validateForm()
    }

    fun updateLocation(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun updatePhoto(uri: Uri?, photoPath: String?) {
        _uiState.update { it.copy(photoUri = uri, photoPath = photoPath) }
    }

    fun toggleCreateIndividually(checked: Boolean) {
        _uiState.update { it.copy(createIndividually = checked) }
    }

    private fun validateForm() {
        val state = _uiState.value
        val year = state.purchaseYearStr.toIntOrNull() ?: 0
        val qty = state.quantityStr.toIntOrNull() ?: 0
        val isValid = state.assetName.isNotBlank() &&
                state.category.isNotBlank() &&
                year > 0 &&
                qty > 0

        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun saveAsset() {
        val state = _uiState.value
        
        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Please fill all required fields") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val quantity = state.quantityStr.toIntOrNull() ?: 1
                val baseSerial = if (state.serialNo.isNotBlank()) {
                    state.serialNo 
                } else {
                    // Generate: CAT-YEAR-001, CAT-YEAR-002, etc.
                    val catPrefix = state.category.take(3).uppercase()
                    val yearSuffix = SimpleDateFormat("yy", Locale.getDefault()).format(Date())
                    val currentCount = assetRepository.getTotalAssetCount()
                    val nextNumber = currentCount + 1
                    val sequentialSuffix = String.format("%03d", nextNumber)
                    "$catPrefix-$yearSuffix$sequentialSuffix"
                }
                
                if (state.createIndividually && quantity > 1) {
                    // Create multiple individual records
                    for (i in 1..quantity) {
                        val asset = Asset(
                            name = "${state.assetName} #$i",
                            category = state.category,
                            serialNo = "$baseSerial-$i",
                            purchaseYear = state.purchaseYearStr.toIntOrNull() ?: 2024,
                            quantity = 1,
                            photoPath = state.photoPath,
                            location = state.location.ifBlank { null },
                            status = "GREEN"
                        )
                        assetRepository.insertAsset(asset)
                    }
                } else {
                    // Create single record with specified quantity
                    val asset = Asset(
                        name = state.assetName,
                        category = state.category,
                        serialNo = baseSerial,
                        purchaseYear = state.purchaseYearStr.toIntOrNull() ?: 2024,
                        quantity = quantity,
                        photoPath = state.photoPath,
                        location = state.location.ifBlank { null },
                        status = "GREEN"
                    )
                    assetRepository.insertAsset(asset)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Asset registered successfully",
                        errorMessage = null,
                        assetName = "",
                        category = "",
                        serialNo = "",
                        purchaseYearStr = "",
                        quantityStr = "",
                        location = "",
                        photoUri = null,
                        photoPath = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to save asset: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }
}
