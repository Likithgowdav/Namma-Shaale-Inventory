package com.namma_shaale.inventory.data.repository

import com.namma_shaale.inventory.data.local.entities.Asset
import com.namma_shaale.inventory.data.local.entities.HealthCheck
import com.namma_shaale.inventory.data.local.entities.IssueLog
import com.namma_shaale.inventory.data.local.entities.RepairRequest
import kotlinx.coroutines.flow.Flow

interface AssetRepository {
    suspend fun insertAsset(asset: Asset)
    suspend fun updateAsset(asset: Asset)
    suspend fun deleteAsset(asset: Asset)
    suspend fun getAssetById(assetId: String): Asset?
    fun getAllAssets(): Flow<List<Asset>>
    fun getAssetsByStatus(status: String): Flow<List<Asset>>
    fun getAssetsByCategory(category: String): Flow<List<Asset>>
    fun searchAssets(searchQuery: String): Flow<List<Asset>>
    suspend fun updateAssetStatus(assetId: String, newStatus: String)
    suspend fun getTotalAssetCount(): Int
    suspend fun getWorkingAssetCount(): Int
    suspend fun getNeedsRepairCount(): Int
    suspend fun getBrokenLostCount(): Int
    suspend fun insertHealthCheck(healthCheck: HealthCheck)
    fun getHealthCheckHistory(assetId: String): Flow<List<HealthCheck>>
    suspend fun getLatestHealthCheck(assetId: String): HealthCheck?
    suspend fun insertIssue(issueLog: IssueLog)
    suspend fun updateIssue(issueLog: IssueLog)
    suspend fun deleteIssue(issueLog: IssueLog)
    fun getIssuesByAsset(assetId: String): Flow<List<IssueLog>>
    fun getIssuesByType(type: String): Flow<List<IssueLog>>
    fun getAllIssues(): Flow<List<IssueLog>>
    suspend fun getIssuesCountThisMonth(): Int
    suspend fun insertRepairRequest(repairRequest: RepairRequest)
    suspend fun updateRepairRequest(repairRequest: RepairRequest)
    suspend fun getRepairRequestByAssetId(assetId: String): RepairRequest?
    fun getPendingRepairRequests(): Flow<List<RepairRequest>>
    fun getAllRepairRequests(): Flow<List<RepairRequest>>
    suspend fun getPendingRepairCount(): Int
}
