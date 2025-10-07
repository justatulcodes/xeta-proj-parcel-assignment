package com.xeta.mws.care.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.xeta.mws.care.R

class ScannerFragment : Fragment() {

    private lateinit var ivBack: ImageView
    private lateinit var btnUpload: LinearLayout
    private lateinit var btnFlashlight: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupClickListeners()
    }

    private fun initViews(view: View) {
        ivBack = view.findViewById(R.id.iv_back)
        btnUpload = view.findViewById(R.id.btn_upload)
        btnFlashlight = view.findViewById(R.id.btn_flashlight)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnUpload.setOnClickListener {
            // Simulate taking a picture and navigating to details
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, DetailsFragment())
                .addToBackStack(null)
                .commit()
        }

        btnFlashlight.setOnClickListener {
            // Toggle flashlight - placeholder functionality
        }
    }
}