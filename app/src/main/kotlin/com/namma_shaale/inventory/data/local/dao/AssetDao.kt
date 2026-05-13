package com.namma_shaale.inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.namma_shaale.inventory.data.local.entities.Asset
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: Asset)

    @Update
    suspend fun update(asset: Asset)

    @Delete
    suspend fun delete(asset: Asset)

    @Query("SELECT * FROM assets WHERE asset_id = :assetId")
    suspend fun getAssetById(assetId: String): Asset?

    @Query("SELECT * FROM assets ORDER BY name ASC")
    fun getAllAssets(): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE status = :status ORDER BY name ASC")
    fun getAssetsByStatus(status: String): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE category = :category ORDER BY name ASC")
    fun getAssetsByCategory(category: String): Flow<List<Asset>>

    @Query("SELECT COUNT(*) FROM assets")
    suspend fun getTotalAssetCount(): Int

    @Query("SELECT COUNT(*) FROM assets WHERE status = 'GREEN'")
    suspend fun getWorkingAssetCount(): Int

    @Query("SELECT COUNT(*) FROM assets WHERE status = 'YELLOW'")
    suspend fun getNeedsRepairCount(): Int

    @Query("SELECT COUNT(*) FROM assets WHERE status = 'RED'")
    suspend fun getBrokenLostCount(): Int

    @Query("SELECT * FROM assets WHERE name LIKE :searchQuery OR serial_no LIKE :searchQuery OR location LIKE :searchQuery ORDER BY name ASC")
    fun searchAssets(searchQuery: String): Flow<List<Asset>>

    @Query("UPDATE assets SET status = :newStatus, last_updated = :timestamp WHERE asset_id = :assetId")
    suspend fun updateAssetStatus(assetId: String, newStatus: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM assets")
    suspend fun deleteAll()
}
