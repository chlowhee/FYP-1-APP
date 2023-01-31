package com.example.jasiribrain.facedetector;

import android.graphics.Bitmap;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.common.MlKitException;
import java.nio.ByteBuffer;

public interface VisionImageProcessor {

    /** Processes a bitmap image. */
    void processBitmap(Bitmap bitmap, GraphicOverlay graphicOverlay);

    /** Processes ImageProxy image data, e.g. used for CameraX live preview case. */
    void processImageProxy(ImageProxy image, GraphicOverlay graphicOverlay) throws MlKitException;

    /** Stops the underlying machine learning model and release resources. */
    void stop();
}
