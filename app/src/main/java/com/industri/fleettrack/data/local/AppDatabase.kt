package com.industri.fleettrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.industri.fleettrack.data.local.dao.DeliveryOrderDao
import com.industri.fleettrack.data.local.dao.SyncQueueDao
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import com.industri.fleettrack.data.local.entity.SyncQueueEntity

@Database(
    entities = [DeliveryOrderEntity::class, SyncQueueEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deliveryOrderDao(): DeliveryOrderDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fleettrack_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
