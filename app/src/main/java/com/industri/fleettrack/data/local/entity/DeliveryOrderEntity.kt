package com.industri.fleettrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delivery_orders")
data class DeliveryOrderEntity(
    @PrimaryKey
    val trackingNumber: String,
    val recipientName: String,
    val recipientPhone: String,
    val recipientAddress: String,
    val codAmount: Long = 0L,
    val status: String = "PENDING", // PENDING, DELIVERED, ON_DELIVERY
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
