package com.industri.fleettrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.industri.fleettrack.data.local.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue WHERE isSynced = 0 ORDER BY timestamp ASC")
    fun getPendingSyncItems(): Flow<List<SyncQueueEntity>>

    @Query("UPDATE sync_queue SET isSynced = 1 WHERE id = :queueId")
    suspend fun markSynced(queueId: Long)
}
