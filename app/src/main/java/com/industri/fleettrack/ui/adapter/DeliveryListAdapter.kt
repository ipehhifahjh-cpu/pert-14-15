package com.industri.fleettrack.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.databinding.ItemDeliveryOrderBinding
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import java.text.NumberFormat
import java.util.Locale

class DeliveryListAdapter(
    private val onDeliverClick: (DeliveryOrderEntity) -> Unit
) : ListAdapter<DeliveryOrderEntity, DeliveryListAdapter.DeliveryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = ItemDeliveryOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeliveryViewHolder(binding, onDeliverClick)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DeliveryViewHolder(
        private val binding: ItemDeliveryOrderBinding,
        private val onDeliverClick: (DeliveryOrderEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: DeliveryOrderEntity) {
            val context = binding.root.context

            // 1. Informasi Dasar Paket & Penerima
            binding.tvTrackingNumber.text = order.trackingNumber
            binding.tvRecipientName.text = order.recipientName
            binding.tvPhoneNumber.text = order.recipientPhone
            binding.tvAddress.text = order.recipientAddress

            // 2. Format & Styling Nominal COD vs NON-COD
            if (order.codAmount > 0) {
                val formattedAmount = NumberFormat.getNumberInstance(Locale("id", "ID")).format(order.codAmount)
                binding.tvCodAmount.text = "COD: Rp $formattedAmount"
                binding.containerCodBadge.setBackgroundResource(R.drawable.bg_badge_cod)
                binding.tvCodAmount.setTextColor(ContextCompat.getColor(context, R.color.amber_700))
                binding.ivCodIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.amber_600))
            } else {
                binding.tvCodAmount.text = "NON-COD / LUNAS"
                binding.containerCodBadge.setBackgroundResource(R.drawable.bg_badge_non_cod)
                binding.tvCodAmount.setTextColor(ContextCompat.getColor(context, R.color.slate_600))
                binding.ivCodIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.slate_400))
            }

            // 3. Styling Status Badge (Pill Shape) & Tombol Aksi Sesuai State
            when (order.status.uppercase(Locale.ROOT)) {
                "DELIVERED" -> {
                    // Badge Styling: Pastel Emerald + Kontras Hijau Tua
                    binding.tvStatusBadge.text = "DELIVERED"
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_delivered)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text))

                    // Button State: Non-aktif & Muted
                    binding.btnMarkDelivered.isEnabled = false
                    binding.btnMarkDelivered.text = "Terkirim"
                    binding.btnMarkDelivered.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.slate_200))
                    binding.btnMarkDelivered.setTextColor(ContextCompat.getColor(context, R.color.slate_500))
                    binding.btnMarkDelivered.setIconResource(R.drawable.ic_check_circle)
                    binding.btnMarkDelivered.iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.emerald_600))
                    binding.btnMarkDelivered.setOnClickListener(null)
                }
                "ON_DELIVERY" -> {
                    // Badge Styling: Pastel Blue + Kontras Biru
                    binding.tvStatusBadge.text = "DALAM PENGIRIMAN"
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_on_delivery)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_on_delivery_text))

                    // Button State: Siap Konfirmasi Pengantaran
                    setupDeliverButtonActive(context, order)
                }
                else -> { // Default "PENDING"
                    // Badge Styling: Pastel Amber + Kontras Oranye Tua
                    binding.tvStatusBadge.text = "PENDING"
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_pending)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text))

                    // Button State: Aktif Emerald Green
                    setupDeliverButtonActive(context, order)
                }
            }
        }

        private fun setupDeliverButtonActive(context: android.content.Context, order: DeliveryOrderEntity) {
            binding.btnMarkDelivered.isEnabled = true
            binding.btnMarkDelivered.text = "Tandai Terkirim"
            binding.btnMarkDelivered.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.emerald_500))
            binding.btnMarkDelivered.setTextColor(ContextCompat.getColor(context, R.color.white))
            binding.btnMarkDelivered.setIconResource(R.drawable.ic_check_circle)
            binding.btnMarkDelivered.iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            binding.btnMarkDelivered.setOnClickListener {
                onDeliverClick(order)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DeliveryOrderEntity>() {
            override fun areItemsTheSame(
                oldItem: DeliveryOrderEntity,
                newItem: DeliveryOrderEntity
            ): Boolean {
                return oldItem.trackingNumber == newItem.trackingNumber
            }

            override fun areContentsTheSame(
                oldItem: DeliveryOrderEntity,
                newItem: DeliveryOrderEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
