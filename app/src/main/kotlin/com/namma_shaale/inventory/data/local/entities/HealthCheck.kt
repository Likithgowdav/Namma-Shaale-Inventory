package com.namma_shaale.inventory.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "health_checks",
    indices = [Index(value = ["asset_id"])],
    foreignKeys = [
        ForeignKey(
            entity = Asset::class,
            parentColumns = ["asset_id"],
            childColumns = ["asset_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HealthCheck(
    @PrimaryKey
    @ColumnInfo(name = "check_id")
    val checkId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "asset_id")
    val assetId: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "checked_date")
    val checkedDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "notes")
    val notes: String? = null
)
