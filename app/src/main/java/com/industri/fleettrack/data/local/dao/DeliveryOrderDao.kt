package com.industri.fleettrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryOrderDao {

    @Query("SELECT * FROM delivery_orders ORDER BY updatedAt DESC")
    fun getAllOrders(): Flow<List<DeliveryOrderEntity>>

    @Query("""
        SELECT * FROM delivery_orders 
        WHERE trackingNumber LIKE '%' || :query || '%' 
           OR recipientName LIKE '%' || :query || '%' 
           OR recipientAddress LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)
    fun searchOrders(query: String): Flow<List<DeliveryOrderEntity>>

    @Query("UPDATE delivery_orders SET status = :status, updatedAt = :updatedAt WHERE trackingNumber = :trackingNumber")
    suspend fun updateStatus(trackingNumber: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<DeliveryOrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: DeliveryOrderEntity)

    @Query("SELECT COUNT(*) FROM delivery_orders")
    fun getTotalOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM delivery_orders WHERE status = :status")
    fun getOrdersCountByStatus(status: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM delivery_orders")
    suspend fun getCountDirect(): Int
}
