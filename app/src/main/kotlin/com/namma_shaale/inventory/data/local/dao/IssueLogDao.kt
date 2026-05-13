package com.namma_shaale.inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.namma_shaale.inventory.data.local.entities.IssueLog
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(issueLog: IssueLog)

    @Delete
    suspend fun delete(issueLog: IssueLog)

    @Query("SELECT * FROM issue_logs WHERE asset_id = :assetId ORDER BY created_date DESC")
    fun getIssuesByAsset(assetId: String): Flow<List<IssueLog>>

    @Query("SELECT * FROM issue_logs WHERE type = :type ORDER BY created_date DESC")
    fun getIssuesByType(type: String): Flow<List<IssueLog>>

    @Query("SELECT * FROM issue_logs ORDER BY created_date DESC")
    fun getAllIssues(): Flow<List<IssueLog>>

    @Query("SELECT COUNT(*) FROM issue_logs WHERE date >= :startTime AND date <= :endTime")
    suspend fun getIssuesCountInPeriod(startTime: Long, endTime: Long): Int

    @Query("DELETE FROM issue_logs")
    suspend fun deleteAll()
}
