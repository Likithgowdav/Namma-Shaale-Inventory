package com.namma_shaale.inventory.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "repair_requests",
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
data class RepairRequest(
    @PrimaryKey
    @ColumnInfo(name = "request_id")
    val requestId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "asset_id")
    val assetId: String,
    @ColumnInfo(name = "raised_date")
    val raisedDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "resolved_date")
    val resolvedDate: Long? = null,
    @ColumnInfo(name = "status")
    val status: String = "PENDING",
    @ColumnInfo(name = "sdmc_notes")
    val sdmcNotes: String? = null,
    @ColumnInfo(name = "cost")
    val cost: Double? = null,
    @ColumnInfo(name = "priority")
    val priority: String = "MEDIUM"
)
