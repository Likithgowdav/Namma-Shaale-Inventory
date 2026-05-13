package com.namma_shaale.inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.namma_shaale.inventory.data.local.entities.HealthCheck
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(healthCheck: HealthCheck)

    @Query("SELECT * FROM health_checks WHERE asset_id = :assetId ORDER BY checked_date DESC")
    fun getHealthCheckHistory(assetId: String): Flow<List<HealthCheck>>

    @Query("SELECT * FROM health_checks WHERE asset_id = :assetId ORDER BY checked_date DESC LIMIT 1")
    suspend fun getLatestHealthCheck(assetId: String): HealthCheck?

    @Query("SELECT * FROM health_checks ORDER BY checked_date DESC")
    fun getAllHealthChecks(): Flow<List<HealthCheck>>

    @Query("DELETE FROM health_checks")
    suspend fun deleteAll()
}
