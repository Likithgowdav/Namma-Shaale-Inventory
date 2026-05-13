package com.namma_shaale.inventory.data.repository

import com.namma_shaale.inventory.data.local.dao.SchoolSettingsDao
import com.namma_shaale.inventory.data.local.entities.SchoolSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

import com.namma_shaale.inventory.data.local.dao.AssetDao
import com.namma_shaale.inventory.data.local.dao.IssueLogDao
import com.namma_shaale.inventory.data.local.dao.RepairRequestDao

@Singleton
class SchoolRepository @Inject constructor(
    private val schoolSettingsDao: SchoolSettingsDao,
    private val assetDao: AssetDao,
    private val issueLogDao: IssueLogDao,
    private val repairRequestDao: RepairRequestDao
) {
    fun getSettings(): Flow<SchoolSettings?> = schoolSettingsDao.getSettings()

    suspend fun updateSchoolName(name: String) {
        val current = schoolSettingsDao.getSettingsSnapshot() ?: SchoolSettings()
        schoolSettingsDao.saveSettings(current.copy(schoolName = name))
    }

    suspend fun updateSchoolAddress(address: String?) {
        val current = schoolSettingsDao.getSettingsSnapshot() ?: SchoolSettings()
        schoolSettingsDao.saveSettings(current.copy(schoolAddress = address))
    }

    suspend fun updateSchoolCode(code: String?) {
        val current = schoolSettingsDao.getSettingsSnapshot() ?: SchoolSettings()
        schoolSettingsDao.saveSettings(current.copy(schoolCode = code))
    }

    suspend fun updateLanguage(languageCode: String) {
        val current = schoolSettingsDao.getSettingsSnapshot() ?: SchoolSettings()
        schoolSettingsDao.saveSettings(current.copy(languageCode = languageCode))
    }

    suspend fun updateDarkMode(isDarkMode: Boolean?) {
        val current = schoolSettingsDao.getSettingsSnapshot() ?: SchoolSettings()
        schoolSettingsDao.saveSettings(current.copy(isDarkMode = isDarkMode))
    }

    suspend fun clearAllData() {
        assetDao.deleteAll()
        issueLogDao.deleteAll()
        repairRequestDao.deleteAll()
    }
}
