package com.xeta.mws.care.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.xeta.mws.care.R
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerFragment : Fragment() {

    private lateinit var ivBack: ImageView
    private lateinit var btnUpload: LinearLayout
    private lateinit var btnFlashlight: LinearLayout
    private lateinit var ivFlashlight: ImageView
    private lateinit var cameraPreview: PreviewView

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var isFlashlightOn = false
    private lateinit var cameraExecutor: ExecutorService

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val TAG = "ScannerFragment"
    }

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
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun initViews(view: View) {
        ivBack = view.findViewById(R.id.iv_back)
        btnUpload = view.findViewById(R.id.btn_upload)
        btnFlashlight = view.findViewById(R.id.btn_flashlight)
        ivFlashlight = view.findViewById(R.id.iv_flashlight)
        cameraPreview = view.findViewById(R.id.camera_preview)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnUpload.setOnClickListener {
            captureImage()
        }

        btnFlashlight.setOnClickListener {
            toggleFlashlight()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(createImageFile()).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                    Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri
                    Log.d(TAG, "Photo capture succeeded: $savedUri")

                    savedUri?.let { uri ->
                        val sampleText = """
                            
                        """.trimIndent()

                        navigateToDetails(uri.path!!, sampleText)
                    }
                }
            }
        )
    }

    private fun navigateToDetails(imagePath: String, extractedText: String) {
        val detailsFragment = DetailsFragment.newInstance(imagePath, extractedText)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, detailsFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun toggleFlashlight() {
        camera?.let { camera ->
            if (camera.cameraInfo.hasFlashUnit()) {
                isFlashlightOn = !isFlashlightOn
                camera.cameraControl.enableTorch(isFlashlightOn)

                val iconRes = if (isFlashlightOn) {
                    R.drawable.ic_flashlight_on
                } else {
                    R.drawable.ic_flashlight
                }
                ivFlashlight.setImageResource(iconRes)
            }
        }
    }

    private fun createImageFile(): File {
        val imageFileName = "JPEG_${System.currentTimeMillis()}"
        val storageDir = requireContext().getExternalFilesDir("Pictures")
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}