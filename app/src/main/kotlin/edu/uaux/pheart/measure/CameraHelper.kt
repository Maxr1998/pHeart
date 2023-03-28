package edu.uaux.pheart.measure

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import java.util.concurrent.Executor

object CameraHelper {
    fun withCameraProvider(context: Context, executor: Executor, onProvider: (ProcessCameraProvider) -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val runnable = Runnable {
            onProvider(cameraProviderFuture.get())
        }
        cameraProviderFuture.addListener(runnable, executor)
    }
}