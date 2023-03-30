package edu.uaux.pheart.measure

import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.face.Face
import edu.uaux.pheart.R
import edu.uaux.pheart.database.ActivityLevel
import edu.uaux.pheart.database.Measurement
import edu.uaux.pheart.database.MeasurementDao
import edu.uaux.pheart.measure.MeasureSettingsViewModel.Companion.DEFAULT_DURATION
import edu.uaux.pheart.util.ext.getParcelableCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import edu.uaux.pheart.util.ext.toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZonedDateTime
import java.util.concurrent.Executor

class MeasureActivity : AppCompatActivity(), MeasurementCallback, KoinComponent {

    companion object {
        const val DEBUG = false

        const val EXTRA_MEASUREMENT_TYPE = "edu.uaux.pheart.measure.EXTRA_MEASUREMENT_TYPE"
        const val EXTRA_ACTIVITY_LEVEL = "edu.uaux.pheart.measure.EXTRA_ACTIVITY_LEVEL"
        const val EXTRA_MEASUREMENT_DURATION = "edu.uaux.pheart.measure.EXTRA_MEASUREMENT_DURATION"
    }

    private val permissionHelper = CameraPermissionHelper(this) {
        startCamera()
    }

    private val measurementDao: MeasurementDao by inject()

    // Intent extras
    private lateinit var measurementType: MeasurementType
    private lateinit var activityLevel: ActivityLevel
    private var measurementDuration: Int = DEFAULT_DURATION

    // Executor
    private lateinit var executor: Executor

    // Views
    private lateinit var statusText: TextView
    private lateinit var cameraPreview: PreviewView
    private lateinit var overlay: OverlayView
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)

        measurementType = intent.extras?.getParcelableCompat(EXTRA_MEASUREMENT_TYPE) ?: MeasurementType.FACE
        activityLevel = intent.extras?.getParcelableCompat(EXTRA_ACTIVITY_LEVEL) ?: ActivityLevel.RELAXED
        measurementDuration = intent.extras?.getInt(EXTRA_MEASUREMENT_DURATION, DEFAULT_DURATION) ?: DEFAULT_DURATION

        executor = ContextCompat.getMainExecutor(this)
        statusText = findViewById(R.id.status_text)
        cameraPreview = findViewById(R.id.camera_preview)
        overlay = findViewById(R.id.overlay)

        // Requests camera permissions and starts the camera when the permission has been granted
        permissionHelper.requireCameraPermission()

        saveButton = findViewById(R.id.button_save_measurement)
        saveButton.setOnClickListener {
            onSaveMeasurement()
        }
    }

    private fun startCamera() {
        CameraHelper.withCameraProvider(this, executor) { cameraProvider ->
            // Unbind previous use-cases
            cameraProvider.unbindAll()

            val previewUseCase = Preview.Builder().build().apply {
                setSurfaceProvider(cameraPreview.surfaceProvider)
            }
            when (measurementType) {
                MeasurementType.FACE -> {
                    val facialHeartRateAnalyzer = FacialHeartRateAnalyzer(executor, this)
                    cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        previewUseCase,
                        facialHeartRateAnalyzer.useCase,
                    )
                }
                MeasurementType.FINGER -> {
                    val fingerHeartRateAnalyzer = FingerHeartRateAnalyzer(executor, this)
                    val camera = cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        previewUseCase,
                        fingerHeartRateAnalyzer.useCase,
                    )
                    camera.cameraControl.enableTorch(true)
                }
            }
        }
    }

    override fun onMeasurementTaken(timestamp: Long, averageLuminance: Double) {
        // TODO: Store measurement
    }

    override fun onShowPoints(points: List<PointF>, imageWidth: Int, imageHeight: Int) {
        if (DEBUG) {
            overlay.points = points
            overlay.imageWidth = imageWidth
            overlay.imageHeight = imageHeight
        }
    }

    override fun onMeasurementCancelled() {
        if (DEBUG) {
            toast("Cancelled")
            overlay.points = emptyList()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.handlePermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onSaveMeasurement() {
        val measurement = Measurement(ZonedDateTime.now().plusDays(180), 80, ActivityLevel.LIGHT_EXERCISE)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                measurementDao.insert(measurement)
            }
        }

        val intent = Intent(this, MeasureResultsActivity::class.java)
        intent.putExtra(MeasureResultsActivity.EXTRA_MEASUREMENT, measurement)

        startActivity(intent)
    }
}