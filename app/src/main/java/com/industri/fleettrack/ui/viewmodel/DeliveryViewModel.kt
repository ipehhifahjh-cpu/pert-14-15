package com.industri.fleettrack.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.industri.fleettrack.data.local.AppDatabase
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import com.industri.fleettrack.data.repository.DeliveryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DeliveryRepository
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = DeliveryRepository(database)
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    val ordersList: StateFlow<List<DeliveryOrderEntity>> = _searchQuery
        .flatMapLatest { query ->
            repository.getOrders(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalCount: StateFlow<Int> = repository.getTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingCount: StateFlow<Int> = repository.getPendingCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val deliveredCount: StateFlow<Int> = repository.getDeliveredCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun markAsDelivered(trackingNumber: String) {
        viewModelScope.launch {
            repository.updateStatusToDelivered(trackingNumber)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshOrders()
            // short delay for smooth UI feedback
            kotlinx.coroutines.delay(600)
            _isRefreshing.value = false
        }
    }
}
