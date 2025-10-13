package com.xeta.mws.care.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
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
        enterCropMode()
        setDefaultAddressValues()
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
        rvRiders = view.findViewById(R.id.rv_riders)

    }

    private fun setupRidersRecyclerView() {
        val riders = listOf(
            AvailableRider("Patrick Frick", "Northeast"),
            AvailableRider("Mary James", "Northeast"),
//            AvailableRider("John Smith", "Southwest"),
//            AvailableRider("Sarah Wilson", "Southeast"),
//            AvailableRider("Mike Johnson", "Northwest")
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

            val optimizedBitmap = preprocessImageForOCR(croppedBitmap)
            performTextRecognition(optimizedBitmap)

            exitCropMode()
        } else {
            Toast.makeText(requireContext(), "Failed to crop image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun preprocessImageForOCR(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(processedBitmap)

        val paint = Paint().apply {
            // Increase contrast
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                set(
                    floatArrayOf(
                        1.5f, 0f, 0f, 0f, -50f,
                        0f, 1.5f, 0f, 0f, -50f,
                        0f, 0f, 1.5f, 0f, -50f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            })
        }

        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return processedBitmap
    }


    private fun performTextRecognition(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                extractedText = visionText.text

                if (extractedText.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "No text detected. Please try again.", Toast.LENGTH_SHORT).show()
                    setDefaultAddressValues()
                } else {
                    updateAddressFromText()
                    Toast.makeText(requireContext(), "Address detected successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to extract text: ${e.message}", Toast.LENGTH_SHORT).show()
                setDefaultAddressValues()
            }
    }

    private fun setDefaultAddressValues() {
        tvAddressLine1.text = "No address detected"
    }


    private fun updateAddressFromText() {
        extractedText?.let { text ->
            val lines = text.lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() && it.length > 2 }

            if (lines.isEmpty()) {
                setDefaultAddressValues()
                return
            }

            var addressLine1 = ""
            var addressLine2 = ""
            var zoneSector = ""

            val streetPattern = Regex(""".*\d+.*(?:street|st|avenue|ave|road|rd|drive|dr|lane|ln|boulevard|blvd|way|court|ct).*""", RegexOption.IGNORE_CASE)
            val postalCodePattern = Regex(""".*\b\d{4,6}\b.*""")
            val cityStatePattern = Regex(""".*(?:NSW|VIC|QLD|SA|WA|TAS|NT|ACT|Sydney|Melbourne|Brisbane|Perth|Adelaide).*""", RegexOption.IGNORE_CASE)
            val zonePattern = Regex(""".*(?:zone|sector|area|district).*\d+.*""", RegexOption.IGNORE_CASE)

            lines.forEach { line ->
                when {
                    addressLine1.isEmpty() && streetPattern.matches(line) -> {
                        addressLine1 = line
                    }

                    addressLine2.isEmpty() && (cityStatePattern.matches(line) || postalCodePattern.matches(line)) -> {
                        addressLine2 = line
                    }

                    zoneSector.isEmpty() && zonePattern.matches(line) -> {
                        zoneSector = line
                    }
                }
            }

            if (addressLine1.isEmpty() && lines.isNotEmpty()) {
                addressLine1 = lines.firstOrNull { it.any { char -> char.isDigit() } }
                    ?: lines.firstOrNull()
                            ?: "Address not detected"
            }

            if (addressLine2.isEmpty() && lines.size > 1) {
                addressLine2 = lines.drop(1).firstOrNull {
                    it.any { char -> char.isLetter() } && it != addressLine1
                } ?: "City/State not detected"
            }

            addressLine1 = cleanAddressText(addressLine1)
            addressLine2 = cleanAddressText(addressLine2)
            zoneSector = if (zoneSector.isNotEmpty()) cleanAddressText(zoneSector) else "Zone: Not specified"

            val rawDetectedText = lines.joinToString()
            tvAddressLine1.text = rawDetectedText

            android.util.Log.d("AddressScanner", "Detected - Line1: $addressLine1, Line2: $addressLine2, Zone: $zoneSector")
        } ?: setDefaultAddressValues()
    }

    private fun cleanAddressText(text: String): String {
        return text
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
            .replace(Regex("[^a-zA-Z0-9\\s,.-]"), "") // Remove unwanted special chars
            .trim()
            .split(" ")
            .joinToString(" ") { word ->
                // Proper capitalization (except for small words)
                if (word.length <= 2 && word.lowercase() !in listOf("st", "rd", "dr", "ln")) {
                    word.uppercase()
                } else {
                    word.lowercase().replaceFirstChar { it.uppercase() }
                }
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