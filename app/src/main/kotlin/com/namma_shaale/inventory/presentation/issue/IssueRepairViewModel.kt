package com.namma_shaale.inventory.presentation.issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma_shaale.inventory.data.local.entities.Asset
import com.namma_shaale.inventory.data.local.entities.IssueLog
import com.namma_shaale.inventory.data.local.entities.RepairRequest
import com.namma_shaale.inventory.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IssueRepairUiState(
    val issues: List<IssueLog> = emptyList(),
    val repairRequests: List<RepairRequest> = emptyList(),
    val assets: List<Asset> = emptyList(),
    val selectedAssetId: String? = null,
    val issueType: String = "",
    val issueReason: String = "",
    val selectedRepairTab: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class IssueRepairViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IssueRepairUiState(isLoading = true))
    val uiState: StateFlow<IssueRepairUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                assetRepository.getAllIssues().collect { issues ->
                    _uiState.update { it.copy(issues = issues, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to load issues: ${e.message}", isLoading = false) }
            }
        }

        viewModelScope.launch {
            try {
                assetRepository.getAllRepairRequests().collect { repairs ->
                    _uiState.update { it.copy(repairRequests = repairs) }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        viewModelScope.launch {
            try {
                assetRepository.getAllAssets().collect { assets ->
                    _uiState.update { it.copy(assets = assets) }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun setSelectedAsset(assetId: String) {
        _uiState.update { it.copy(selectedAssetId = assetId) }
    }

    fun setIssueType(type: String) {
        _uiState.update { it.copy(issueType = type) }
    }

    fun setIssueReason(reason: String) {
        _uiState.update { it.copy(issueReason = reason) }
    }

    fun logIssue() {
        val state = _uiState.value
        if (state.selectedAssetId == null || state.issueType.isEmpty() || state.issueReason.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please fill all fields") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val issue = IssueLog(
                    assetId = state.selectedAssetId,
                    type = state.issueType,
                    date = System.currentTimeMillis(),
                    reason = state.issueReason
                )
                assetRepository.insertIssue(issue)

                val newStatus = when (state.issueType) {
                    "LOST", "STOLEN" -> "RED"
                    else -> "YELLOW"
                }
                assetRepository.updateAssetStatus(state.selectedAssetId, newStatus)

                val repairRequest = RepairRequest(
                    assetId = state.selectedAssetId,
                    raisedDate = System.currentTimeMillis(),
                    status = "PENDING",
                    priority = if (newStatus == "RED") "HIGH" else "MEDIUM"
                )
                assetRepository.insertRepairRequest(repairRequest)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Issue logged successfully",
                        selectedAssetId = null,
                        issueType = "",
                        issueReason = "",
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to log issue: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteIssue(issue: IssueLog) {
        viewModelScope.launch {
            try {
                assetRepository.deleteIssue(issue)
                _uiState.update { it.copy(successMessage = "Issue deleted successfully") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete issue: ${e.message}") }
            }
        }
    }

    fun updateIssue(issue: IssueLog, newType: String, newReason: String) {
        viewModelScope.launch {
            try {
                val updatedIssue = issue.copy(type = newType, reason = newReason)
                assetRepository.updateIssue(updatedIssue)
                _uiState.update { it.copy(successMessage = "Issue updated successfully") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to update issue: ${e.message}") }
            }
        }
    }

    fun markRepairResolved(repairRequestId: String, notes: String) {
        viewModelScope.launch {
            try {
                val request = _uiState.value.repairRequests.find { it.requestId == repairRequestId }
                    ?: return@launch

                val updatedRequest = request.copy(
                    status = "RESOLVED",
                    resolvedDate = System.currentTimeMillis(),
                    sdmcNotes = notes
                )
                assetRepository.updateRepairRequest(updatedRequest)
                assetRepository.updateAssetStatus(request.assetId, "GREEN")

                _uiState.update {
                    it.copy(successMessage = "Repair marked as resolved")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to resolve repair: ${e.message}")
                }
            }
        }
    }

    fun setRepairTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedRepairTab = tabIndex) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }
}
