package edu.uaux.pheart.measure

import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.util.Size
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.common.math.DoubleMath.log2
import edu.uaux.pheart.R
import edu.uaux.pheart.database.ActivityLevel
import edu.uaux.pheart.database.Measurement
import edu.uaux.pheart.database.MeasurementDao
import edu.uaux.pheart.measure.MeasureSettingsViewModel.Companion.DEFAULT_DURATION
import edu.uaux.pheart.util.ext.getParcelableCompat
import edu.uaux.pheart.util.ext.toast
import edu.uaux.pheart.util.fftFreq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jtransforms.fft.DoubleFFT_1D
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZonedDateTime
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.floor
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

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
    private lateinit var toolbar: Toolbar
    private lateinit var timerText: TextView
    private lateinit var cameraPreview: PreviewView
    private lateinit var overlay: OverlayView
    private lateinit var heartRateContainer: ViewGroup
    private lateinit var heartRateText: TextView
    private lateinit var abortButton: Button
    private lateinit var redoButton: Button
    private lateinit var saveButton: Button

    // Camera
    private lateinit var previewUseCase: Preview

    // State
    private val measureState = MutableStateFlow(MeasureState.NONE)
    private val luminanceMeasurements: MutableList<LuminanceMeasurement> = mutableListOf()
    private var lastHeartRate: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)

        // Read intent extras
        measurementType = intent.extras?.getParcelableCompat(EXTRA_MEASUREMENT_TYPE) ?: MeasurementType.FACE
        activityLevel = intent.extras?.getParcelableCompat(EXTRA_ACTIVITY_LEVEL) ?: ActivityLevel.RELAXING
        measureDuration = intent.extras?.getInt(EXTRA_MEASURE_DURATION, DEFAULT_DURATION) ?: DEFAULT_DURATION

        executor = ContextCompat.getMainExecutor(this)
        toolbar = findViewById(R.id.toolbar)
        timerText = findViewById(R.id.timer_text)
        cameraPreview = findViewById(R.id.camera_preview)
        overlay = findViewById(R.id.overlay)
        heartRateContainer = findViewById(R.id.bpm_container)
        heartRateText = heartRateContainer.findViewById(R.id.bpm_text)

        previewUseCase = Preview.Builder().apply {
            setTargetResolution(Size(480, 640))
        }.build().apply {
            setSurfaceProvider(cameraPreview.surfaceProvider)
        }

        abortButton = findViewById(R.id.button_abort_measurement)
        abortButton.setOnClickListener {
            measureState.tryEmit(MeasureState.ABORTED)
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
                    updateButtonState(state)
                    when (state) {
                        MeasureState.NONE -> permissionHelper.requireCameraPermission()
                        MeasureState.IDLE -> launch {
                            toolbar.setTitle(R.string.measurement_state_get_ready)
                            heartRateContainer.isVisible = false
                            cameraPreview.isVisible = true
                            startPreview()
                            showTimer(5)
                            measureState.emit(MeasureState.MEASURING)
                        }
                        MeasureState.MEASURING -> launch {
                            toolbar.setTitle(R.string.measurement_state_measuring)
                            startMeasurement()
                            showTimer(measureDuration)
                            measureState.emit(MeasureState.FINISHED)
                        }
                        MeasureState.FINISHED -> launch {
                            toolbar.setTitle(R.string.measurement_state_complete)
                            cameraPreview.isVisible = false
                            stopCamera()
                            lastHeartRate = computeHeartRate()
                            heartRateText.text = lastHeartRate.toString()
                            heartRateContainer.isVisible = true
                        }
                        MeasureState.ABORTED -> launch {
                            stopCamera()
                            finish()
                        }
                    }
                }
            }
        }
    }

    private suspend fun showTimer(seconds: Int) {
        timerText.isVisible = true
        for (i in seconds downTo 1) {
            timerText.text = "$i"
            delay(1.seconds)
        }
        timerText.isVisible = false
    }

    private fun updateButtonState(state: MeasureState) {
        abortButton.isVisible = state != MeasureState.FINISHED
        abortButton.isEnabled = state != MeasureState.NONE
        redoButton.isVisible = state == MeasureState.FINISHED
        saveButton.isEnabled = state == MeasureState.FINISHED
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

    private fun computeHeartRate(): Int {
        val nearestPowerOfTwo = 2 shl (floor(log2(luminanceMeasurements.size.toDouble())).toInt() - 1)
        val analyzedMeasurements = luminanceMeasurements.take(nearestPowerOfTwo)
        val fft = DoubleFFT_1D(nearestPowerOfTwo.toLong())

        // Calculate the time deltas between the measurements
        val timeDeltas = analyzedMeasurements.mapIndexed { index, measurement ->
            if (index == 0) 0 else measurement.timestamp - analyzedMeasurements[index - 1].timestamp
        }.drop(1)
        val medianTimeDelta = timeDeltas.sorted()[timeDeltas.size / 2]

        // Calculate the FFT of the luminance measurements
        val fftData = analyzedMeasurements.map(LuminanceMeasurement::averageLuminance).toDoubleArray()
        fft.realForward(fftData) // output will be stored in fftData

        // We only care about the magnitude of the imaginary parts
        for (i in fftData.indices) {
            fftData[i] = abs(fftData[i])
        }
        val frequencies = fftFreq(fftData.size, medianTimeDelta.nanoseconds.toDouble(DurationUnit.SECONDS))

        // We only care about frequencies between 1.0 and 3.0 Hz, which are natural heart rates
        val minFreqIndex = frequencies.indexOfFirst { f -> f > 1.0 }
        val maxFreqIndex = frequencies.indexOfFirst { f -> f > 3.0 }

        val mostCommonFrequencyIndex = fftData.withIndex()
            .filter { e -> e.index in minFreqIndex until maxFreqIndex }
            .maxBy { e -> e.value }
            .index

        // Convert Hz to bpm
        return (frequencies[mostCommonFrequencyIndex] * 60.0).toInt()
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
            val measurement = Measurement(ZonedDateTime.now(), lastHeartRate, activityLevel)

            withContext(Dispatchers.IO) {
                measurementDao.insert(measurement)
            }

            Intent(this@MeasureActivity, MeasureResultsActivity::class.java).apply {
                putExtra(MeasureResultsActivity.EXTRA_MEASUREMENT, measurement)
                startActivity(this)
            }
            finish()
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