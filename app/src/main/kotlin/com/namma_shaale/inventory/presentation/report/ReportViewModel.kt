package com.namma_shaale.inventory.presentation.report

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma_shaale.inventory.data.repository.AssetRepository
import com.namma_shaale.inventory.data.repository.SchoolRepository
import com.namma_shaale.inventory.data.util.PdfReportGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportUiState(
    val reportText: String = "",
    val reportUri: Uri? = null,
    val isLoading: Boolean = false,
    val isGenerated: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val schoolRepository: SchoolRepository,
    private val pdfReportGenerator: PdfReportGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun generateReport() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val assets = assetRepository.getAllAssets().first()
                val schoolName = schoolRepository.getSettings().first()?.schoolName ?: "Namma Shaale"
                val uri = pdfReportGenerator.generateInventoryReport(assets, schoolName)

                if (uri != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reportUri = uri,
                            isGenerated = true,
                            successMessage = "Report generated successfully!"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Failed to create PDF file.")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }

    fun downloadReport() {
        val uri = _uiState.value.reportUri ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = pdfReportGenerator.downloadToPublicStorage(uri)
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    successMessage = if (success) "Report saved to Downloads folder!" else "Failed to save report.",
                    errorMessage = if (success) null else "Storage error occurred."
                ) 
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }
}
