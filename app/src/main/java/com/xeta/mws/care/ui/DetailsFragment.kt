package com.xeta.mws.care.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.xeta.mws.care.R

class DetailsFragment : Fragment() {

    private lateinit var ivBack: ImageView
    private lateinit var btnAssignPatrick: Button
    private lateinit var btnCancelPatrick: Button
    private lateinit var btnAssignMary: Button
    private lateinit var btnCancelMary: Button
    private lateinit var btnResend: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupClickListeners()
    }

    private fun initViews(view: View) {
        ivBack = view.findViewById(R.id.iv_back)
        btnAssignPatrick = view.findViewById(R.id.btn_assign_patrick)
        btnCancelPatrick = view.findViewById(R.id.btn_cancel_patrick)
        btnAssignMary = view.findViewById(R.id.btn_assign_mary)
        btnCancelMary = view.findViewById(R.id.btn_cancel_mary)
        btnResend = view.findViewById(R.id.btn_resend)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnAssignPatrick.setOnClickListener {
            // Assign parcel to Patrick Frick
            // Show success dialog or navigate back
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnCancelPatrick.setOnClickListener {
            // Cancel assignment for Patrick
        }

        btnAssignMary.setOnClickListener {
            // Assign parcel to Mary James
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnCancelMary.setOnClickListener {
            // Cancel assignment for Mary
        }

        btnResend.setOnClickListener {
            // Resend delivery details
        }
    }
}