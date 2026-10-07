package com.industri.fleettrack.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databinding.ActivityManifestBinding
import com.industri.fleettrack.ui.adapter.DeliveryListAdapter
import com.industri.fleettrack.ui.viewmodel.DeliveryViewModel
import kotlinx.coroutines.launch

class ManifestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManifestBinding
    private val viewModel: DeliveryViewModel by viewModels()
    private lateinit var adapter: DeliveryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManifestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchBar()
        setupSwipeToRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = DeliveryListAdapter { order ->
            viewModel.markAsDelivered(order.trackingNumber)
        }
        binding.rvDeliveryOrders.apply {
            layoutManager = LinearLayoutManager(this@ManifestActivity)
            adapter = this@ManifestActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable?.toString().orEmpty()
            viewModel.setSearchQuery(query)
            binding.btnClearSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setColorSchemeResources(
            com.example.R.color.emerald_500,
            com.example.R.color.slate_800
        )
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.ordersList.collect { orders ->
                        adapter.submitList(orders)
                        binding.layoutEmpty.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.isRefreshing.collect { refreshing ->
                        binding.swipeRefresh.isRefreshing = refreshing
                    }
                }

                launch {
                    viewModel.totalCount.collect { count ->
                        binding.tvTotalCount.text = count.toString()
                    }
                }

                launch {
                    viewModel.pendingCount.collect { count ->
                        binding.tvPendingCount.text = count.toString()
                    }
                }

                launch {
                    viewModel.deliveredCount.collect { count ->
                        binding.tvDeliveredCount.text = count.toString()
                    }
                }
            }
        }
    }
}
