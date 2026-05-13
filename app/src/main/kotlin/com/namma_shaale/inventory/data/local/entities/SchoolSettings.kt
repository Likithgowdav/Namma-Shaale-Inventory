package com.namma_shaale.inventory.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "school_settings")
data class SchoolSettings(
    @PrimaryKey val id: Int = 1,
    val schoolName: String = "Namma Shaale",
    val schoolAddress: String? = null,
    val schoolCode: String? = null,
    val languageCode: String = "en",
    val isDarkMode: Boolean? = null
)
