package com.xeta.mws.care.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xeta.mws.care.R
import com.xeta.mws.care.data.AvailableRider

class RidersAdapter(
    private val riders: List<AvailableRider>,
    private val onAssignClick: (AvailableRider) -> Unit,
    private val onCancelClick: (AvailableRider) -> Unit
) : RecyclerView.Adapter<RidersAdapter.RiderViewHolder>() {

    inner class RiderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRiderName: TextView = view.findViewById(R.id.tv_rider_name)
        val tvRiderArea: TextView = view.findViewById(R.id.tv_rider_area)
        val btnAssign: Button = view.findViewById(R.id.btn_assign)
        val btnCancel: Button = view.findViewById(R.id.btn_cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_available_rider, parent, false)
        return RiderViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiderViewHolder, position: Int) {
        val rider = riders[position]
        
        holder.tvRiderName.text = rider.name
        holder.tvRiderArea.text = "Area : ${rider.area}"
        
        holder.btnAssign.setOnClickListener {
            onAssignClick(rider)
        }
        
        holder.btnCancel.setOnClickListener {
            onCancelClick(rider)
        }
    }

    override fun getItemCount() = riders.size
}