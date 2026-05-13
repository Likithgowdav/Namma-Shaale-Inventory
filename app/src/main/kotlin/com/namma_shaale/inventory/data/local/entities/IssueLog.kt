package com.namma_shaale.inventory.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "issue_logs",
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
data class IssueLog(
    @PrimaryKey
    @ColumnInfo(name = "issue_id")
    val issueId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "asset_id")
    val assetId: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "reason")
    val reason: String,
    @ColumnInfo(name = "reporter")
    val reporter: String? = null,
    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis()
)
