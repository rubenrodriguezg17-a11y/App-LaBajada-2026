package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val buyerId: String,
    val searchQuery: String,
    val timestamp: Long = System.currentTimeMillis()
)