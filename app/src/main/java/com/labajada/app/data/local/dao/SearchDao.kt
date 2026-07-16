package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.labajada.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(query: SearchHistoryEntity)

    @Query("SELECT * FROM search_history WHERE buyerId = :buyerId ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearchHistory(buyerId: String): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history WHERE buyerId = :buyerId")
    suspend fun clearAllSearchHistory(buyerId: String)
}