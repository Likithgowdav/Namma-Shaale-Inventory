package com.namma_shaale.inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.namma_shaale.inventory.data.local.entities.RepairRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repairRequest: RepairRequest)

    @Update
    suspend fun update(repairRequest: RepairRequest)

    @Delete
    suspend fun delete(repairRequest: RepairRequest)

    @Query("SELECT * FROM repair_requests WHERE asset_id = :assetId AND status != 'RESOLVED' LIMIT 1")
    suspend fun getRepairRequestByAssetId(assetId: String): RepairRequest?

    @Query("SELECT * FROM repair_requests WHERE status != 'RESOLVED' ORDER BY raised_date ASC")
    fun getPendingRepairRequests(): Flow<List<RepairRequest>>

    @Query("SELECT * FROM repair_requests WHERE status = 'RESOLVED' ORDER BY resolved_date DESC LIMIT 30")
    fun getRecentlyResolvedRequests(): Flow<List<RepairRequest>>

    @Query("SELECT * FROM repair_requests ORDER BY raised_date DESC")
    fun getAllRepairRequests(): Flow<List<RepairRequest>>

    @Query("SELECT COUNT(*) FROM repair_requests WHERE status != 'RESOLVED'")
    suspend fun getPendingRepairCount(): Int

    @Query("UPDATE repair_requests SET status = :newStatus, resolved_date = :resolvedDate WHERE request_id = :requestId")
    suspend fun updateRepairStatus(requestId: String, newStatus: String, resolvedDate: Long?)

    @Query("DELETE FROM repair_requests")
    suspend fun deleteAll()
}
