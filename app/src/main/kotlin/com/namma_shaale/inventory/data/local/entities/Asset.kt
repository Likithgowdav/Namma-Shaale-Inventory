/**
 * Asset: The core data model representing a school inventory item.
 * Includes fields for tracking name, category, location, and health status.
 */
package com.namma_shaale.inventory.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "assets", indices = [
    androidx.room.Index(value = ["status"]),
    androidx.room.Index(value = ["category"])
])
data class Asset(
    @PrimaryKey
    @ColumnInfo(name = "asset_id")
    val assetId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "serial_no")
    val serialNo: String? = null,
    @ColumnInfo(name = "purchase_year")
    val purchaseYear: Int,
    @ColumnInfo(name = "quantity")
    val quantity: Int = 1,
    @ColumnInfo(name = "status")
    val status: String = "GREEN",
    @ColumnInfo(name = "photo_path")
    val photoPath: String? = null,
    @ColumnInfo(name = "location")
    val location: String? = null,
    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)
