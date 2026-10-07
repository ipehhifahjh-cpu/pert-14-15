package com.industri.fleettrack.data.repository

import com.industri.fleettrack.data.local.AppDatabase
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import com.industri.fleettrack.data.local.entity.SyncQueueEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DeliveryRepository(private val db: AppDatabase) {

    private val deliveryDao = db.deliveryOrderDao()
    private val syncDao = db.syncQueueDao()

    fun getOrders(searchQuery: String): Flow<List<DeliveryOrderEntity>> {
        return if (searchQuery.isBlank()) {
            deliveryDao.getAllOrders()
        } else {
            deliveryDao.searchOrders(searchQuery.trim())
        }
    }

    fun getTotalCount(): Flow<Int> = deliveryDao.getTotalOrdersCount()
    fun getPendingCount(): Flow<Int> = deliveryDao.getOrdersCountByStatus("PENDING")
    fun getDeliveredCount(): Flow<Int> = deliveryDao.getOrdersCountByStatus("DELIVERED")

    suspend fun updateStatusToDelivered(trackingNumber: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        // 1. Update status locally in Room (Offline-First)
        deliveryDao.updateStatus(trackingNumber, "DELIVERED", now)

        // 2. Queue for background sync to remote server
        syncDao.enqueue(
            SyncQueueEntity(
                trackingNumber = trackingNumber,
                newStatus = "DELIVERED",
                action = "STATUS_UPDATE",
                timestamp = now,
                isSynced = false
            )
        )
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        if (deliveryDao.getCountDirect() == 0) {
            seedSampleOrders()
        }
    }

    suspend fun refreshOrders() = withContext(Dispatchers.IO) {
        // Simulating remote fetch & merge into Room database
        if (deliveryDao.getCountDirect() == 0) {
            seedSampleOrders()
        }
    }

    private suspend fun seedSampleOrders() {
        val initialList = listOf(
            DeliveryOrderEntity(
                trackingNumber = "CRB-98234-JKT",
                recipientName = "Ahmad Maulana",
                recipientPhone = "0812-2245-8891",
                recipientAddress = "Jl. Kartini No. 18, Kejaksan, Kota Cirebon",
                codAmount = 145000L,
                status = "PENDING",
                notes = "Pagar hitam, hubungi sebelum antar"
            ),
            DeliveryOrderEntity(
                trackingNumber = "CRB-84112-BDG",
                recipientName = "Dewi Anggraeni",
                recipientPhone = "0857-9801-4432",
                recipientAddress = "Jl. Cipto Mangunkusumo No. 74, Kesambi, Cirebon",
                codAmount = 0L,
                status = "PENDING",
                notes = "Non-COD (Sudah Lunas)"
            ),
            DeliveryOrderEntity(
                trackingNumber = "CRB-77561-SUB",
                recipientName = "Hendro Wijaya",
                recipientPhone = "0813-8876-1200",
                recipientAddress = "Perum Pilang Perdana Blok C No. 12, Kedawung, Cirebon",
                codAmount = 320000L,
                status = "PENDING",
                notes = "Uang pas disiapkan"
            ),
            DeliveryOrderEntity(
                trackingNumber = "CRB-65320-KNG",
                recipientName = "Siti Fatimah",
                recipientPhone = "0821-4321-9988",
                recipientAddress = "Jl. Kanggraksan No. 29, Harjamukti, Kota Cirebon",
                codAmount = 78000L,
                status = "DELIVERED",
                notes = "Diterima oleh yang bersangkutan"
            ),
            DeliveryOrderEntity(
                trackingNumber = "CRB-55912-SMG",
                recipientName = "Rian Hidayat",
                recipientPhone = "0878-1122-3344",
                recipientAddress = "Jl. Pasuketan No. 5, Lemahwungkuk, Kota Cirebon",
                codAmount = 210000L,
                status = "PENDING",
                notes = "Toko Grosir Berkah"
            ),
            DeliveryOrderEntity(
                trackingNumber = "CRB-44109-YOG",
                recipientName = "Ratna Sari",
                recipientPhone = "0852-6677-8899",
                recipientAddress = "Jl. Lawanggada No. 88, Pekalipan, Kota Cirebon",
                codAmount = 0L,
                status = "DELIVERED",
                notes = "Dititipkan di satpam kantor"
            )
        )
        deliveryDao.insertAll(initialList)
    }
}
