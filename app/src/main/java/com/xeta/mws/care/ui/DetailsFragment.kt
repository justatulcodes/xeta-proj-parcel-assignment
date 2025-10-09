package com.xeta.mws.care.ui

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.xeta.mws.care.R

class DetailsFragment : Fragment() {

    private lateinit var ivBack: ImageView
    private lateinit var ivScannedImage: ImageView
    private lateinit var btnAssignPatrick: Button
    private lateinit var btnCancelPatrick: Button
    private lateinit var btnAssignMary: Button
    private lateinit var btnCancelMary: Button
    private lateinit var btnResend: Button
    private lateinit var tvAddressLine1: TextView
    private lateinit var tvAddressLine2: TextView
    private lateinit var tvZoneSector: TextView

    private var imagePath: String? = null
    private var extractedText: String? = null

    companion object {
        private const val ARG_IMAGE_PATH = "image_path"
        private const val ARG_EXTRACTED_TEXT = "extracted_text"

        fun newInstance(imagePath: String, extractedText: String): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle().apply {
                putString(ARG_IMAGE_PATH, imagePath)
                putString(ARG_EXTRACTED_TEXT, extractedText)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imagePath = it.getString(ARG_IMAGE_PATH)
            extractedText = it.getString(ARG_EXTRACTED_TEXT)
        }
    }

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
        loadCapturedImage()
        updateAddressFromText()
    }

    private fun initViews(view: View) {
        ivBack = view.findViewById(R.id.iv_back)
        ivScannedImage = view.findViewById(R.id.iv_scanned_image)
        btnAssignPatrick = view.findViewById(R.id.btn_assign_patrick)
        btnCancelPatrick = view.findViewById(R.id.btn_cancel_patrick)
        btnAssignMary = view.findViewById(R.id.btn_assign_mary)
        btnCancelMary = view.findViewById(R.id.btn_cancel_mary)
        btnResend = view.findViewById(R.id.btn_resend)
        tvAddressLine1 = view.findViewById(R.id.tv_address_line1)
        tvAddressLine2 = view.findViewById(R.id.tv_address_line2)
        tvZoneSector = view.findViewById(R.id.tv_zone_sector)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnAssignPatrick.setOnClickListener {
            showRiderAssignedDialog("Patrick Frick")
        }

        btnCancelPatrick.setOnClickListener {
            // Cancel assignment for Patrick
        }

        btnAssignMary.setOnClickListener {
            showRiderAssignedDialog("Mary James")
        }

        btnCancelMary.setOnClickListener {
            // Cancel assignment for Mary
        }

        btnResend.setOnClickListener {
            // Resend delivery details
        }
    }

    private fun loadCapturedImage() {
        imagePath?.let { path ->
            try {
                // First, decode the image
                val bitmap = BitmapFactory.decodeFile(path)

                if (bitmap != null) {
                    // Read EXIF orientation data
                    val exif = ExifInterface(path)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )

                    // Determine rotation angle
                    val rotationAngle = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }

                    // Apply rotation if needed
                    val rotatedBitmap = if (rotationAngle != 0f) {
                        val matrix = Matrix().apply {
                            postRotate(rotationAngle)
                        }
                        android.graphics.Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                    } else {
                        bitmap
                    }

                    ivScannedImage.setImageBitmap(rotatedBitmap)

                    // Clean up original bitmap if we created a rotated version
                    if (rotatedBitmap != bitmap) {
                        bitmap.recycle()
                    }
                }
            } catch (e: Exception) {
                // If image loading fails, keep the placeholder
                e.printStackTrace()
            }
        }
    }

    private fun updateAddressFromText() {
        // Parse extracted text and update address fields
        extractedText?.let { text ->
            // Simple parsing - in real app you'd use more sophisticated text processing
            val lines = text.lines().filter { it.trim().isNotEmpty() }

            // Try to extract address information
            var addressLine1 = "123, 5th Avenue"
            var addressLine2 = "Sydney, NSW 2000, Australia"
            var zoneSector = "South Zone - Sector 15"

            // Look for patterns in the text (basic implementation)
            lines.forEach { line ->
                when {
                    line.contains("address", ignoreCase = true) ||
                            line.contains("street", ignoreCase = true) ||
                            line.matches(Regex(".*\\d+.*[Aa]venue.*")) -> {
                        addressLine1 = line.trim()
                    }

                    line.contains("NSW", ignoreCase = true) ||
                            line.contains("Sydney", ignoreCase = true) ||
                            line.contains("Australia", ignoreCase = true) -> {
                        addressLine2 = line.trim()
                    }
                }
            }

            tvAddressLine1.text = addressLine1
            tvAddressLine2.text = addressLine2
            tvZoneSector.text = zoneSector
        }
    }

    private fun showRiderAssignedDialog(riderName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Rider Assigned!")
            .setMessage("Parcel has been assigned to $riderName")
            .setPositiveButton("Next") { dialog, _ ->
                dialog.dismiss()
                navigateToHome()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToHome() {
        // Clear back stack and navigate to home
        requireActivity().supportFragmentManager.popBackStack(
            null,
            androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, com.xeta.mws.care.ui.HomeFragment())
            .commit()
    }
}