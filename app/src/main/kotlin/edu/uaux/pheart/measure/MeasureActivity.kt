package edu.uaux.pheart.measure

import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import edu.uaux.pheart.R
import edu.uaux.pheart.database.ActivityLevel
import edu.uaux.pheart.database.Measurement
import edu.uaux.pheart.database.MeasurementDao
import edu.uaux.pheart.measure.MeasureSettingsViewModel.Companion.DEFAULT_DURATION
import edu.uaux.pheart.util.ext.getParcelableCompat
import edu.uaux.pheart.util.ext.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZonedDateTime
import java.util.concurrent.Executor
import kotlin.time.Duration.Companion.seconds

class MeasureActivity : AppCompatActivity(), MeasureCallback, KoinComponent {

    companion object {
        const val DEBUG = false

        const val EXTRA_MEASUREMENT_TYPE = "edu.uaux.pheart.measure.EXTRA_MEASUREMENT_TYPE"
        const val EXTRA_ACTIVITY_LEVEL = "edu.uaux.pheart.measure.EXTRA_ACTIVITY_LEVEL"
        const val EXTRA_MEASURE_DURATION = "edu.uaux.pheart.measure.EXTRA_MEASUREMENT_DURATION"
    }

    // Dependencies
    private val measurementDao: MeasurementDao by inject()

    private val permissionHelper = CameraPermissionHelper(this) { granted ->
        if (granted) measureState.tryEmit(MeasureState.IDLE) else finish()
    }

    // Intent extras
    private lateinit var measurementType: MeasurementType
    private lateinit var activityLevel: ActivityLevel
    private var measureDuration: Int = DEFAULT_DURATION

    // Executor
    private lateinit var executor: Executor

    // Views
    private lateinit var statusText: TextView
    private lateinit var cameraPreview: PreviewView
    private lateinit var overlay: OverlayView
    private lateinit var redoButton: Button
    private lateinit var saveButton: Button

    // Camera
    private lateinit var previewUseCase: Preview

    // State
    private val measureState = MutableStateFlow(MeasureState.NONE)
    private val luminanceMeasurements: MutableList<LuminanceMeasurement> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)

        // Read intent extras
        measurementType = intent.extras?.getParcelableCompat(EXTRA_MEASUREMENT_TYPE) ?: MeasurementType.FACE
        activityLevel = intent.extras?.getParcelableCompat(EXTRA_ACTIVITY_LEVEL) ?: ActivityLevel.RELAXING
        measureDuration = intent.extras?.getInt(EXTRA_MEASURE_DURATION, DEFAULT_DURATION) ?: DEFAULT_DURATION

        executor = ContextCompat.getMainExecutor(this)
        statusText = findViewById(R.id.status_text)
        cameraPreview = findViewById(R.id.camera_preview)
        overlay = findViewById(R.id.overlay)

        previewUseCase = Preview.Builder().build().apply {
            setSurfaceProvider(cameraPreview.surfaceProvider)
        }

        redoButton = findViewById(R.id.button_redo_measurement)
        redoButton.setOnClickListener {
            measureState.tryEmit(MeasureState.IDLE)
        }

        saveButton = findViewById(R.id.button_save_measurement)
        saveButton.setOnClickListener {
            onSaveMeasurement()
        }

        handleMeasurementState()
    }

    private fun handleMeasurementState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                measureState.collect { state ->
                    when (state) {
                        MeasureState.NONE -> permissionHelper.requireCameraPermission()
                        MeasureState.IDLE -> launch {
                            enableButtons(false)
                            cameraPreview.isVisible = true
                            startPreview()
                            showCountdown(5)
                            measureState.emit(MeasureState.MEASURING)
                        }
                        MeasureState.MEASURING -> launch {
                            startMeasurement()
                            showCountdown(measureDuration)
                            measureState.emit(MeasureState.FINISHED)
                        }
                        MeasureState.FINISHED -> launch {
                            statusText.text = null
                            cameraPreview.isVisible = false
                            stopCamera()
                            computeResults()
                            enableButtons(true)
                        }
                    }
                }
            }
        }
    }

    private suspend fun showCountdown(seconds: Int) {
        for (i in seconds downTo 1) {
            statusText.text = "$i"
            delay(1.seconds)
        }
    }

    private fun enableButtons(enabled: Boolean) {
        redoButton.isEnabled = enabled
        saveButton.isEnabled = enabled
    }

    private suspend fun getCameraProvider(): ProcessCameraProvider {
        return ProcessCameraProvider.getInstance(this).await()
    }

    private suspend fun startPreview() {
        val cameraProvider = getCameraProvider()
        val cameraSelector = when (measurementType) {
            MeasurementType.FACE -> CameraSelector.DEFAULT_FRONT_CAMERA
            MeasurementType.FINGER -> CameraSelector.DEFAULT_BACK_CAMERA
        }
        val camera = cameraProvider.bindToLifecycle(
            this,
            cameraSelector,
            previewUseCase,
        )
        if (measurementType == MeasurementType.FINGER) {
            camera.cameraControl.enableTorch(true)
        }
    }

    private suspend fun startMeasurement() {
        luminanceMeasurements.clear()
        val cameraProvider = getCameraProvider()
        when (measurementType) {
            MeasurementType.FACE -> {
                val facialHeartRateAnalyzer = FacialHeartRateAnalyzer(executor, this)
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    facialHeartRateAnalyzer.useCase,
                )
            }
            MeasurementType.FINGER -> {
                val fingerHeartRateAnalyzer = FingerHeartRateAnalyzer(executor, this)
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    fingerHeartRateAnalyzer.useCase,
                )
            }
        }
    }

    private suspend fun stopCamera() {
        val cameraProvider = getCameraProvider()
        cameraProvider.unbindAll()
    }

    private fun computeResults() {
        val measurements = luminanceMeasurements
        // TODO: compute heart rate from measurements
    }

    override fun onLuminanceMeasured(value: LuminanceMeasurement) {
        luminanceMeasurements += value
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
        lifecycleScope.launch {
            // TODO: replace stub measurement with real one
            val measurement = Measurement(ZonedDateTime.now().plusDays(180), 80, ActivityLevel.EXERCISING)

            withContext(Dispatchers.IO) {
                measurementDao.insert(measurement)
            }

            Intent(this@MeasureActivity, MeasureResultsActivity::class.java).apply {
                putExtra(MeasureResultsActivity.EXTRA_MEASUREMENT, measurement)
                startActivity(this)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        // Finish activity and abort measurement if the user leaves the activity
        if (measureState.value == MeasureState.MEASURING) {
            toast(R.string.toast_warning_measurement_cancelled)
            finish()
        }
    }
}