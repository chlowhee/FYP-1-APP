package com.example.jasiribrain.facedetector

import android.content.Context
import android.util.Log
import com.example.jasiribrain.data.JasiriDataHolder
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectorProcessor(context: Context, detectorOptions: FaceDetectorOptions?) :
    VisionProcessorBase<List<Face>>(context) {

    private val detector: FaceDetector

    init {
        val options = detectorOptions
            ?: FaceDetectorOptions.Builder()
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

        detector = FaceDetection.getClient(options)

        Log.v(MANUAL_TESTING_LOG, "Face detector options: $options")
    }

    override fun stop() {
        super.stop()
        detector.close()
    }

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun onSuccess(faces: List<Face>, graphicOverlay: GraphicOverlay) {
        for (face in faces) {
//            graphicOverlay.add(FaceGraphic(graphicOverlay, face)) //no nd graphics
//            logExtrasForTesting(face)

            if (JasiriDataHolder.faceTrackingIsWanted.value) {
                faceTracker(face)
            }
            if (JasiriDataHolder.eyeDetectionIsWanted.value) {
                sleepyEyesDetector(face)
                Log.v(MANUAL_TESTING_LOG, "eyesClosed counter: $eyesClosedCounter")
                if (eyesClosedCounter > sleepingThreshold) {
                    eyesClosedCounter = 0
                    JasiriDataHolder.setEyesAreSleepy(true)
                }
            }
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
        private const val sleepingThreshold = 650
        private var eyesClosedCounter = 0

        private fun faceTracker(face: Face?) {
            if (face == null) return

            when (face.boundingBox.centerX()) {
                in 0..100 -> {
                    Log.d(MANUAL_TESTING_LOG, "FACE tracker: Left x3")
                    JasiriDataHolder.setFacePosition(1)
                }
                in 101..159 ->{
                    Log.d(MANUAL_TESTING_LOG, "FACE tracker: Left x2")
                    JasiriDataHolder.setFacePosition(2)
                }
                in 160..210 -> {
                    Log.d(MANUAL_TESTING_LOG, "FACE tracker: Left x1")
                    JasiriDataHolder.setFacePosition(3)
                }
                in 265..300 -> {
                    Log.d(MANUAL_TESTING_LOG, "FACE tracker: Right x1")
                    JasiriDataHolder.setFacePosition(4)
                }
                in 301..355 -> {
                    Log.d(MANUAL_TESTING_LOG, "FACE tracker: Right x2")
                    JasiriDataHolder.setFacePosition(5)
                }
                in 356..500 -> {
                    Log.d(MANUAL_TESTING_LOG, "FACE tracker: Right x3")
                    JasiriDataHolder.setFacePosition(6)
                }
            }
            JasiriDataHolder.setFaceTrackingIsWanted(false)
            Log.d(MANUAL_TESTING_LOG, "FACE TRACKER off")
        }

        private fun sleepyEyesDetector(face: Face?) {
            if (face == null) return
            val leftEye = face.leftEyeOpenProbability
            val rightEye = face.rightEyeOpenProbability

            if (leftEye == null || rightEye == null) return

            if ((leftEye < 0.4) && (rightEye < 0.4)) {
                // eyes are closed
                eyesClosedCounter++
            }
        }

        private fun logExtrasForTesting(face: Face?) {
            if (face != null) {
                Log.v(
                    MANUAL_TESTING_LOG, //TODO: Check relative to center
                    "face bounding box: " + face.boundingBox.flattenToString() + face.boundingBox.centerX()
                )
                Log.v(
                    MANUAL_TESTING_LOG, //TODO: left && right eye close check. Close - <0.5
                    "face left eye open probability: " + face.leftEyeOpenProbability
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face right eye open probability: " + face.rightEyeOpenProbability
                )
            }
        }
    }
}