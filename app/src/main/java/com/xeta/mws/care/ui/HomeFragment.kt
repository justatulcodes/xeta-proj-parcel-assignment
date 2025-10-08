package com.xeta.mws.care.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.xeta.mws.care.R
import com.xeta.mws.care.data.Rider
import com.xeta.mws.care.data.RiderStatus
import com.xeta.mws.care.ui.adapter.RiderAdapter

class HomeFragment : Fragment() {

    private lateinit var rvRiders: RecyclerView
    private lateinit var fabScanner: FloatingActionButton
    private lateinit var tvViewAll: TextView
    private lateinit var riderAdapter: RiderAdapter

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
        loadSampleData()
    }

    private fun initViews(view: View) {
        rvRiders = view.findViewById(R.id.rv_riders)
        fabScanner = view.findViewById(R.id.fab_scanner)
        tvViewAll = view.findViewById(R.id.tv_view_all)
    }

    private fun setupRecyclerView() {
        riderAdapter = RiderAdapter { rider ->
            // Handle rider item click
        }

        rvRiders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = riderAdapter
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

    private fun loadSampleData() {
        val sampleRiders = listOf(
            Rider("1", "John", "North", 5, RiderStatus.ONLINE),
            Rider("2", "John", "East", 4, RiderStatus.ONLINE),
            Rider("3", "John", "West", 4, RiderStatus.OFFLINE),
            Rider("4", "John", "South", 2, RiderStatus.BUSY),
            Rider("5", "John", "North", 5, RiderStatus.BUSY),
            Rider("6", "John", "East", 2, RiderStatus.OFFLINE),
        )

        riderAdapter.submitList(sampleRiders)
    }
}