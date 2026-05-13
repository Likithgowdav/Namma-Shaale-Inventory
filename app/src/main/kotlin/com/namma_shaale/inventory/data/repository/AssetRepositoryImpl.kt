package com.namma_shaale.inventory.data.repository

import com.namma_shaale.inventory.data.local.AppDatabase
import com.namma_shaale.inventory.data.local.entities.Asset
import com.namma_shaale.inventory.data.local.entities.HealthCheck
import com.namma_shaale.inventory.data.local.entities.IssueLog
import com.namma_shaale.inventory.data.local.entities.RepairRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : AssetRepository {
    override suspend fun insertAsset(asset: Asset) = appDatabase.assetDao().insert(asset)
    override suspend fun updateAsset(asset: Asset) = appDatabase.assetDao().update(asset)
    override suspend fun deleteAsset(asset: Asset) = appDatabase.assetDao().delete(asset)
    override suspend fun getAssetById(assetId: String): Asset? = appDatabase.assetDao().getAssetById(assetId)
    override fun getAllAssets(): Flow<List<Asset>> = appDatabase.assetDao().getAllAssets()
    override fun getAssetsByStatus(status: String): Flow<List<Asset>> = appDatabase.assetDao().getAssetsByStatus(status)
    override fun getAssetsByCategory(category: String): Flow<List<Asset>> = appDatabase.assetDao().getAssetsByCategory(category)
    override fun searchAssets(searchQuery: String): Flow<List<Asset>> = appDatabase.assetDao().searchAssets("%${searchQuery}%")
    override suspend fun updateAssetStatus(assetId: String, newStatus: String) = appDatabase.assetDao().updateAssetStatus(assetId, newStatus)
    override suspend fun getTotalAssetCount(): Int = appDatabase.assetDao().getTotalAssetCount()
    override suspend fun getWorkingAssetCount(): Int = appDatabase.assetDao().getWorkingAssetCount()
    override suspend fun getNeedsRepairCount(): Int = appDatabase.assetDao().getNeedsRepairCount()
    override suspend fun getBrokenLostCount(): Int = appDatabase.assetDao().getBrokenLostCount()
    
    override suspend fun insertHealthCheck(healthCheck: HealthCheck) = appDatabase.healthCheckDao().insert(healthCheck)
    override fun getHealthCheckHistory(assetId: String): Flow<List<HealthCheck>> = appDatabase.healthCheckDao().getHealthCheckHistory(assetId)
    override suspend fun getLatestHealthCheck(assetId: String): HealthCheck? = appDatabase.healthCheckDao().getLatestHealthCheck(assetId)
    
    override suspend fun insertIssue(issueLog: IssueLog) = appDatabase.issueLogDao().insert(issueLog)
    override suspend fun updateIssue(issueLog: IssueLog) = appDatabase.issueLogDao().insert(issueLog) // REPLACE strategy handles update
    override suspend fun deleteIssue(issueLog: IssueLog) = appDatabase.issueLogDao().delete(issueLog)
    override fun getIssuesByAsset(assetId: String): Flow<List<IssueLog>> = appDatabase.issueLogDao().getIssuesByAsset(assetId)
    override fun getIssuesByType(type: String): Flow<List<IssueLog>> = appDatabase.issueLogDao().getIssuesByType(type)
    override fun getAllIssues(): Flow<List<IssueLog>> = appDatabase.issueLogDao().getAllIssues()
    override suspend fun getIssuesCountThisMonth(): Int {
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
        return appDatabase.issueLogDao().getIssuesCountInPeriod(thirtyDaysAgo, now)
    }
    
    override suspend fun insertRepairRequest(repairRequest: RepairRequest) = appDatabase.repairRequestDao().insert(repairRequest)
    override suspend fun updateRepairRequest(repairRequest: RepairRequest) = appDatabase.repairRequestDao().update(repairRequest)
    override suspend fun getRepairRequestByAssetId(assetId: String): RepairRequest? = appDatabase.repairRequestDao().getRepairRequestByAssetId(assetId)
    override fun getPendingRepairRequests(): Flow<List<RepairRequest>> = appDatabase.repairRequestDao().getPendingRepairRequests()
    override fun getAllRepairRequests(): Flow<List<RepairRequest>> = appDatabase.repairRequestDao().getAllRepairRequests()
    override suspend fun getPendingRepairCount(): Int = appDatabase.repairRequestDao().getPendingRepairCount()
}
