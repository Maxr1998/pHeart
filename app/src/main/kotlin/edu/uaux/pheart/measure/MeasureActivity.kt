package edu.uaux.pheart.measure

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.Face
import edu.uaux.pheart.R
import java.util.concurrent.Executor

class MeasureActivity : AppCompatActivity(), FaceDetectionHelper.Callback {

    private val permissionHelper = CameraPermissionHelper(this) {
        startCamera()
    }

    private lateinit var executor: Executor
    private lateinit var faceDetectionHelper: FaceDetectionHelper
    private lateinit var statusText: TextView
    private lateinit var cameraPreview: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)
        executor = ContextCompat.getMainExecutor(this)
        faceDetectionHelper = FaceDetectionHelper(executor, this)

        statusText = findViewById(R.id.status_text)
        cameraPreview = findViewById(R.id.camera_preview)

        // Requests camera permissions and starts the camera when the permission has been granted
        permissionHelper.requireCameraPermission()
    }

    private fun startCamera() {
        CameraHelper.withCameraProvider(this, executor) { cameraProvider ->
            cameraProvider.unbindAll()

            val previewUseCase = Preview.Builder().build().apply {
                setSurfaceProvider(cameraPreview.surfaceProvider)
            }
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                previewUseCase,
                faceDetectionHelper.useCase,
            )
        }
    }

    private val clearTextRunnable = Runnable {
        statusText.text = null
    }

    @SuppressLint("SetTextI18n")
    override fun onFacesDetected(faces: List<Face>) {
        statusText.text = "${faces.size} faces detected"
        statusText.removeCallbacks(clearTextRunnable)
        statusText.postDelayed(clearTextRunnable, 300)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.handlePermissionsResult(requestCode, permissions, grantResults)
    }
}