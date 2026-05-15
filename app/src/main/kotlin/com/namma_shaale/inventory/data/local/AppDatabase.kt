/**
 * AppDatabase: The central persistence layer for Namma Shaale Inventory.
 * Uses Room to manage Assets, Health Checks, and Issue Logs locally.
 */
package com.namma_shaale.inventory.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.namma_shaale.inventory.data.local.dao.*
import com.namma_shaale.inventory.data.local.entities.*

@Database(
    entities = [Asset::class, HealthCheck::class, IssueLog::class, RepairRequest::class, SchoolSettings::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun healthCheckDao(): HealthCheckDao
    abstract fun issueLogDao(): IssueLogDao
    abstract fun repairRequestDao(): RepairRequestDao
    abstract fun schoolSettingsDao(): SchoolSettingsDao

    companion object {
        private const val DATABASE_NAME = "namma_shaale_inventory.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
    }
}
