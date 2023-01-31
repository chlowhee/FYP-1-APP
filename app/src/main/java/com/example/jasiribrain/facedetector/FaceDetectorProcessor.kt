package com.example.jasiribrain.facedetector

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

/** Face Detector Demo.  */
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
//            graphicOverlay.add(FaceGraphic(graphicOverlay, face)) // no nd graphics
            logExtrasForTesting(face)
//            faceTracker(face)
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
        private fun faceTracker(face: Face?) { //TODO
            // Euler & closeness of face will change
            // middleVal - ans; -ve: move left; +ve: move right
            if (face == null) return
            //maybe update results to data holder to allow controller to move
        }
        private fun logExtrasForTesting(face: Face?) {
            if (face != null) {
                Log.v(
                    MANUAL_TESTING_LOG, //TODO: Check relative to center
                    "face bounding box: " + face.boundingBox.flattenToString()
                )
                val faceCenter = face.boundingBox.flattenToString().substring(0,3).toInt()
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face bound 1 int: $faceCenter"
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face Euler Angle X: " + face.headEulerAngleX
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face Euler Angle Y: " + face.headEulerAngleY
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face Euler Angle Z: " + face.headEulerAngleZ
                )
                //TODO: REMOVE IF RLY DO NOT NEED LANDMARK POSITIONS
                // All landmarks
//                val landMarkTypes = intArrayOf(
//                    FaceLandmark.RIGHT_EYE,
//                    FaceLandmark.LEFT_EYE
//                )
//                val landMarkTypesStrings = arrayOf(
//                    "RIGHT_EYE",
//                    "LEFT_EYE"
//                )
//                for (i in landMarkTypes.indices) {
//                    val landmark = face.getLandmark(landMarkTypes[i])
//                    if (landmark == null) {
//                        Log.v(
//                            MANUAL_TESTING_LOG,
//                            "No landmark of type: " + landMarkTypesStrings[i] + " has been detected"
//                        )
//                    } else {
//                        val landmarkPosition = landmark.position
//                        val landmarkPositionStr =
//                            String.format(Locale.US, "x: %f , y: %f", landmarkPosition.x, landmarkPosition.y)
//                        Log.v(
//                            MANUAL_TESTING_LOG,
//                            "Position for face landmark: " +
//                                    landMarkTypesStrings[i] +
//                                    " is :" +
//                                    landmarkPositionStr
//                        )
//                    }
//                }
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