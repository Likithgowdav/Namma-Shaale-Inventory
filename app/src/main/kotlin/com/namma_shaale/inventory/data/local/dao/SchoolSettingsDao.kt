package com.namma_shaale.inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.namma_shaale.inventory.data.local.entities.SchoolSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolSettingsDao {
    @Query("SELECT * FROM school_settings WHERE id = 1")
    fun getSettings(): Flow<SchoolSettings?>

    @Query("SELECT * FROM school_settings WHERE id = 1")
    suspend fun getSettingsSnapshot(): SchoolSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SchoolSettings)
}
