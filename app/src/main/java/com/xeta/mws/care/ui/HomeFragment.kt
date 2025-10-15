package com.xeta.mws.care.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.xeta.mws.care.R
import com.xeta.mws.care.data.network.RetrofitClient
import com.xeta.mws.care.data.network.SummaryResponse
import com.xeta.mws.care.ui.adapter.EmployeeSummaryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var rvRiders: RecyclerView
    private lateinit var fabScanner: FloatingActionButton
    private lateinit var tvViewAll: TextView
    private lateinit var employeeSummaryAdapter: EmployeeSummaryAdapter
    private lateinit var tvTotalParcels: TextView
    private lateinit var tvTotalRiders: TextView
    private lateinit var tvAvgParcelsPerRider: TextView
    private lateinit var tvTotalRegions: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupClickListeners()
        fetchSummaryData()
    }

    private fun initViews(view: View) {
        rvRiders = view.findViewById(R.id.rv_riders)
        fabScanner = view.findViewById(R.id.fab_scanner)
        tvViewAll = view.findViewById(R.id.tv_view_all)
        tvTotalParcels = view.findViewById(R.id.tv_total_parcels)
        tvTotalRiders = view.findViewById(R.id.tv_total_riders)
        tvAvgParcelsPerRider = view.findViewById(R.id.tv_avg_parcels_per_rider)
        tvTotalRegions = view.findViewById(R.id.tv_total_regions)
    }

    private fun setupRecyclerView() {
        employeeSummaryAdapter = EmployeeSummaryAdapter()

        rvRiders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = employeeSummaryAdapter
        }
    }

    private fun setupClickListeners() {
        fabScanner.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, ScannerFragment())
                .addToBackStack(null)
                .commit()
        }

        tvViewAll.setOnClickListener {
        }
    }

    private fun fetchSummaryData() {
        RetrofitClient.apiService.getSummary().enqueue(object : Callback<SummaryResponse> {
            override fun onResponse(call: Call<SummaryResponse>, response: Response<SummaryResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val summary = response.body()!!
                    updateSummaryCards(summary)
                    employeeSummaryAdapter.submitList(summary.per_employee_summary)
                } else {
                    Toast.makeText(requireContext(), "Failed to load summary", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SummaryResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateSummaryCards(summary: SummaryResponse) {
        tvTotalParcels.text = summary.overall_summary.total_parcels.toString()
        tvTotalRiders.text = summary.overall_summary.total_riders.toString()
        tvAvgParcelsPerRider.text = summary.overall_summary.average_parcels_per_rider.toString()
        tvTotalRegions.text = summary.overall_summary.total_regions.toString()
    }
}