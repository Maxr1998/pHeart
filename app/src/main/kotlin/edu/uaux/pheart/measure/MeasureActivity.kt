package edu.uaux.pheart.measure

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.Face
import edu.uaux.pheart.R
import edu.uaux.pheart.database.ActivityLevel
import edu.uaux.pheart.measure.MeasureSettingsViewModel.Companion.DEFAULT_DURATION
import edu.uaux.pheart.util.ext.getParcelableCompat
import java.util.concurrent.Executor

class MeasureActivity : AppCompatActivity(), FacialHeartRateAnalyzer.Callback {

    companion object {
        const val EXTRA_MEASUREMENT_TYPE = "edu.uaux.pheart.measure.EXTRA_MEASUREMENT_TYPE"
        const val EXTRA_ACTIVITY_LEVEL = "edu.uaux.pheart.measure.EXTRA_ACTIVITY_LEVEL"
        const val EXTRA_MEASUREMENT_DURATION = "edu.uaux.pheart.measure.EXTRA_MEASUREMENT_DURATION"
    }

    private val permissionHelper = CameraPermissionHelper(this) {
        startCamera()
    }

    private lateinit var measurementType: MeasurementType
    private lateinit var activityLevel: ActivityLevel
    private var measurementDuration: Int = DEFAULT_DURATION
    private lateinit var executor: Executor
    private lateinit var facialHeartRateAnalyzer: FacialHeartRateAnalyzer
    private lateinit var statusText: TextView
    private lateinit var cameraPreview: PreviewView
    private lateinit var overlay: OverlayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)

        measurementType = intent.extras?.getParcelableCompat(EXTRA_MEASUREMENT_TYPE) ?: MeasurementType.FACE
        activityLevel = intent.extras?.getParcelableCompat(EXTRA_ACTIVITY_LEVEL) ?: ActivityLevel.RELAXED
        measurementDuration = intent.extras?.getInt(EXTRA_MEASUREMENT_DURATION, DEFAULT_DURATION) ?: DEFAULT_DURATION

        executor = ContextCompat.getMainExecutor(this)
        facialHeartRateAnalyzer = FacialHeartRateAnalyzer(executor, this)

        statusText = findViewById(R.id.status_text)
        cameraPreview = findViewById(R.id.camera_preview)
        overlay = findViewById(R.id.overlay)

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
                *when (measurementType) {
                    MeasurementType.FACE -> arrayOf(previewUseCase, facialHeartRateAnalyzer.useCase)
                    MeasurementType.FINGER -> arrayOf(previewUseCase)
                },
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

    override fun onMeasurementTaken(averageLuminance: Float) {
        // TODO: Store measurement
    }

    override fun onShowPoints(points: List<PointF>, imageWidth: Int, imageHeight: Int) {
        overlay.points = points
        overlay.imageWidth = imageWidth
        overlay.imageHeight = imageHeight
    }

    override fun onMeasurementCancelled() {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        overlay.points = emptyList()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.handlePermissionsResult(requestCode, permissions, grantResults)
    }
}