package com.xeta.mws.care.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xeta.mws.care.R
import com.xeta.mws.care.data.Rider
import com.xeta.mws.care.data.RiderStatus

class RiderAdapter(
    private val onItemClick: (Rider) -> Unit
) : ListAdapter<Rider, RiderAdapter.RiderViewHolder>(RiderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rider, parent, false)
        return RiderViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiderViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class RiderViewHolder(
        private val itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvRiderName: TextView = itemView.findViewById(R.id.tv_rider_name)
        private val tvZone: TextView = itemView.findViewById(R.id.tv_zone)
        private val tvOrders: TextView = itemView.findViewById(R.id.tv_orders)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_status)

        fun bind(rider: Rider, onItemClick: (Rider) -> Unit) {
            tvRiderName.text = rider.name
            tvZone.text = rider.zone
            tvOrders.text = rider.orders.toString()

            // Set status text and background color
            tvStatus.text = when (rider.status) {
                RiderStatus.ONLINE -> itemView.context.getString(R.string.online)
                RiderStatus.OFFLINE -> itemView.context.getString(R.string.offline)
                RiderStatus.AVAILABLE -> itemView.context.getString(R.string.available)
            }

            // Set status background color
            val statusColor = when (rider.status) {
                RiderStatus.ONLINE -> R.color.status_green
                RiderStatus.OFFLINE -> R.color.status_red
                RiderStatus.AVAILABLE -> R.color.status_orange
            }
            tvStatus.setBackgroundColor(
                ContextCompat.getColor(itemView.context, statusColor)
            )

            itemView.setOnClickListener { onItemClick(rider) }
        }
    }

    private class RiderDiffCallback : DiffUtil.ItemCallback<Rider>() {
        override fun areItemsTheSame(oldItem: Rider, newItem: Rider): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Rider, newItem: Rider): Boolean {
            return oldItem == newItem
        }
    }
}