package com.xeta.mws.care.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImageView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.xeta.mws.care.R
import com.xeta.mws.care.data.AvailableRider
import com.xeta.mws.care.ui.adapter.RidersAdapter

class DetailsFragment : Fragment() {

    private lateinit var ivBack: ImageView
    private lateinit var ivScannedImage: ImageView
    private lateinit var cropImageView: CropImageView
    private lateinit var btnCropImage: Button
    private lateinit var btnCropConfirm: Button
    private lateinit var btnCancelCrop: Button
    private lateinit var cropButtonsLayout: LinearLayout
    private lateinit var rvRiders: RecyclerView
    private lateinit var ridersAdapter: RidersAdapter
    private lateinit var btnResend: Button
    private lateinit var tvAddressLine1: TextView
    private lateinit var tvAddressLine2: TextView
    private lateinit var tvZoneSector: TextView

    private var imagePath: String? = null
    private var extractedText: String? = null
    private var currentBitmap: Bitmap? = null
    private var isCropMode = false

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
        setupRidersRecyclerView()
        loadCapturedImage()
        updateAddressFromText()
    }

    private fun initViews(view: View) {
        ivBack = view.findViewById(R.id.iv_back)
        ivScannedImage = view.findViewById(R.id.iv_scanned_image)
        cropImageView = view.findViewById(R.id.cropImageView)
        btnCropImage = view.findViewById(R.id.btn_crop_image)
        btnCropConfirm = view.findViewById(R.id.btn_crop_confirm)
        btnCancelCrop = view.findViewById(R.id.btn_cancel_crop)
        cropButtonsLayout = view.findViewById(R.id.crop_buttons_layout)
        btnResend = view.findViewById(R.id.btn_resend)
        tvAddressLine1 = view.findViewById(R.id.tv_address_line1)
        tvAddressLine2 = view.findViewById(R.id.tv_address_line2)
        tvZoneSector = view.findViewById(R.id.tv_zone_sector)
        rvRiders = view.findViewById(R.id.rv_riders)

    }

    private fun setupRidersRecyclerView() {
        val riders = listOf(
            AvailableRider("Patrick Frick", "Northeast"),
            AvailableRider("Mary James", "Northeast"),
            AvailableRider("John Smith", "Southwest"),
            AvailableRider("Sarah Wilson", "Southeast"),
            AvailableRider("Mike Johnson", "Northwest")
        )

        ridersAdapter = RidersAdapter(
            riders = riders,
            onAssignClick = { rider ->
                showRiderAssignedDialog(rider.name)
            },
            onCancelClick = { rider ->
            }
        )

        rvRiders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ridersAdapter
        }
    }


    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnCropImage.setOnClickListener {
            enterCropMode()
        }

        btnCancelCrop.setOnClickListener {
            exitCropMode()
        }

        btnCropConfirm.setOnClickListener {
            performCropAndScan()
        }

        btnResend.setOnClickListener {
        }
    }

    private fun loadCapturedImage() {
        imagePath?.let { path ->
            try {
                val bitmap = BitmapFactory.decodeFile(path)

                if (bitmap != null) {
                    val exif = ExifInterface(path)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )

                    val rotationAngle = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }

                    val rotatedBitmap = if (rotationAngle != 0f) {
                        val matrix = Matrix().apply {
                            postRotate(rotationAngle)
                        }
                        Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                    } else {
                        bitmap
                    }

                    currentBitmap = rotatedBitmap
                    ivScannedImage.setImageBitmap(rotatedBitmap)

                    if (rotatedBitmap != bitmap) {
                        bitmap.recycle()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enterCropMode() {
        currentBitmap?.let { bitmap ->
            isCropMode = true

            ivScannedImage.visibility = View.GONE
            cropImageView.visibility = View.VISIBLE
            cropButtonsLayout.visibility = View.VISIBLE
            btnCropImage.visibility = View.GONE

            cropImageView.setImageBitmap(bitmap)
            cropImageView.scaleType = CropImageView.ScaleType.CENTER_INSIDE
        }
    }

    private fun exitCropMode() {
        isCropMode = false

        cropImageView.visibility = View.GONE
        cropButtonsLayout.visibility = View.GONE
        ivScannedImage.visibility = View.VISIBLE
        btnCropImage.visibility = View.VISIBLE

        cropImageView.clearImage()
    }

    private fun performCropAndScan() {
        val croppedBitmap = cropImageView.getCroppedImage()

        if (croppedBitmap != null) {
            currentBitmap = croppedBitmap
            ivScannedImage.setImageBitmap(croppedBitmap)

            performTextRecognition(croppedBitmap)

            exitCropMode()
        } else {
            Toast.makeText(requireContext(), "Failed to crop image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performTextRecognition(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                extractedText = visionText.text
                updateAddressFromText()
                Toast.makeText(requireContext(), "Text extracted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to extract text", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateAddressFromText() {
        extractedText?.let { text ->
            val lines = text.lines().filter { it.trim().isNotEmpty() }

            var addressLine1 = "123, 5th Avenue"
            var addressLine2 = "Sydney, NSW 2000, Australia"
            var zoneSector = "South Zone - Sector 15"

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
        requireActivity().supportFragmentManager.popBackStack(
            null,
            androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, HomeFragment())
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentBitmap?.recycle()
        currentBitmap = null
    }
}