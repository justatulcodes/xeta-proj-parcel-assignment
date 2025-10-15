package com.xeta.mws.care.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xeta.mws.care.R
import com.xeta.mws.care.data.network.PerEmployeeSummary

class EmployeeSummaryAdapter : ListAdapter<PerEmployeeSummary, EmployeeSummaryAdapter.EmployeeSummaryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeSummaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rider, parent, false)
        return EmployeeSummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeSummaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EmployeeSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
        private val tvDesignation: TextView = itemView.findViewById(R.id.tv_designation)
        private val tvParcelsAssigned: TextView = itemView.findViewById(R.id.tv_parcels_assigned)

        fun bind(summary: PerEmployeeSummary) {
            tvFullName.text = summary.full_name
            tvDesignation.text = summary.designation
            tvParcelsAssigned.text = summary.parcels_assigned.toString()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PerEmployeeSummary>() {
        override fun areItemsTheSame(oldItem: PerEmployeeSummary, newItem: PerEmployeeSummary): Boolean {
            return oldItem.full_name == newItem.full_name && oldItem.designation == newItem.designation
        }
        override fun areContentsTheSame(oldItem: PerEmployeeSummary, newItem: PerEmployeeSummary): Boolean {
            return oldItem == newItem
        }
    }
}

