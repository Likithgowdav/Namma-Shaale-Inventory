package com.namma_shaale.inventory.data.di

import android.content.Context
import androidx.room.Room
import com.namma_shaale.inventory.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "namma_shaale_inventory.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideAssetDao(appDatabase: AppDatabase) = appDatabase.assetDao()

    @Singleton
    @Provides
    fun provideHealthCheckDao(appDatabase: AppDatabase) = appDatabase.healthCheckDao()

    @Singleton
    @Provides
    fun provideIssueLogDao(appDatabase: AppDatabase) = appDatabase.issueLogDao()

    @Singleton
    @Provides
    fun provideRepairRequestDao(appDatabase: AppDatabase) = appDatabase.repairRequestDao()

    @Singleton
    @Provides
    fun provideSchoolSettingsDao(appDatabase: AppDatabase) = appDatabase.schoolSettingsDao()
}
