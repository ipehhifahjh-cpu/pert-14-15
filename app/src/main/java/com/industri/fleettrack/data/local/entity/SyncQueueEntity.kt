package com.industri.fleettrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val trackingNumber: String,
    val newStatus: String,
    val action: String = "UPDATE_STATUS",
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
