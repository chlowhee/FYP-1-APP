package com.example.jasiribrain.facedetector

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jasiribrain.databinding.PreviewCameraBinding
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceDetectorOptions.CLASSIFICATION_MODE_ALL

class CameraPreview: Fragment() {

    private var _binding: PreviewCameraBinding? = null
    private val binding get() = _binding!!

    private val lensFacing = CameraSelector.LENS_FACING_FRONT
    private var cameraSelector: CameraSelector? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false

    private val TAG = "cam preview"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PreviewCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[CameraXViewModel::class.java]
            .processCameraProvider
            .observe(
                viewLifecycleOwner,
                Observer { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    bindCameraUseCase()
                }
            )

    }

    override fun onPause() {
        super.onPause()
        imageProcessor?.run { this.stop() }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run { this.stop() }
    }

    private fun bindCameraUseCase() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        val targetResolution = android.util.Size.parseSize("640x480")
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(binding.previewView!!.getSurfaceProvider())
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, previewUseCase)
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        imageProcessor =
            try {
                val faceDetectorOp = faceDetectorOption()
                FaceDetectorProcessor(requireContext(), faceDetectorOp)
            } catch (e: Exception) {
                Log.e(TAG, "Can not create image processor")
                Toast.makeText(
                    requireContext(),
                    "Can not create image processor: " + e.localizedMessage,
                    Toast.LENGTH_LONG
                )
                    .show()
                return
            }

        val builder = ImageAnalysis.Builder()
        val targetResolution = android.util.Size.parseSize("640x480")
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        analysisUseCase = builder.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            ContextCompat.getMainExecutor(requireContext())
        ) { imageProxy: ImageProxy ->
            if (needUpdateGraphicOverlayImageSourceInfo) {
                val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                if (rotationDegrees == 0 || rotationDegrees == 180) {
                    binding.graphicOverlay.setImageSourceInfo(
                        imageProxy.width,
                        imageProxy.height,
                        isImageFlipped
                    )
                } else {
                    binding.graphicOverlay.setImageSourceInfo(
                        imageProxy.height,
                        imageProxy.width,
                        isImageFlipped
                    )
                }
                needUpdateGraphicOverlayImageSourceInfo = false
            }
            try {
                imageProcessor!!.processImageProxy(imageProxy, binding.graphicOverlay)
            } catch (e: MlKitException) {
                Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, analysisUseCase)
    }

    private fun faceDetectorOption(): FaceDetectorOptions {
        val optionsBuilder = FaceDetectorOptions.Builder()
            .setClassificationMode(CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.3F)
        return optionsBuilder.build()
    }

}