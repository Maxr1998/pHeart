package edu.uaux.pheart.measure

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButtonToggleGroup
import de.Maxr1998.modernpreferences.views.ModernSeekBar
import edu.uaux.pheart.R
import edu.uaux.pheart.database.ActivityLevel

class MeasureSettingsFragment : Fragment() {

    private val viewModel: MeasureSettingsViewModel by activityViewModels()

    private lateinit var measurementTypeButtonGroup: MaterialButtonToggleGroup
    private lateinit var measurementTypeDescription: TextView
    private lateinit var activityLevelButtonGroup: MaterialButtonToggleGroup
    private lateinit var measurementDurationSlider: ModernSeekBar
    private lateinit var startButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_measure_settings, container, false)
    }

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        measurementTypeButtonGroup = view.findViewById(R.id.button_group_measurement_type)
        measurementTypeDescription = view.findViewById(R.id.measurement_type_description)
        activityLevelButtonGroup = view.findViewById(R.id.button_group_activity_level)
        measurementDurationSlider = view.findViewById(R.id.measurement_duration_slider)
        startButton = view.findViewById(R.id.button_start_measurement)

        measurementTypeButtonGroup.check(
            when (viewModel.measurementType) {
                MeasurementType.FACE -> R.id.button_measurement_type_face
                MeasurementType.FINGER -> R.id.button_measurement_type_finger
            },
        )
        measurementTypeButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                viewModel.measurementType = when (checkedId) {
                    R.id.button_measurement_type_face -> MeasurementType.FACE
                    R.id.button_measurement_type_finger -> MeasurementType.FINGER
                    else -> error("Illegal button clicked")
                }
                updateMeasurementDescription()
            }
        }
        updateMeasurementDescription()
        activityLevelButtonGroup.check(
            when (viewModel.activityLevel) {
                ActivityLevel.RELAXED -> R.id.button_activity_level_relaxed
                ActivityLevel.SEATED -> R.id.button_activity_level_seated
                ActivityLevel.LIGHT_EXERCISE -> R.id.button_activity_level_light_exercise
                ActivityLevel.HEAVY_EXERCISE -> R.id.button_activity_level_heavy_exercise
            },
        )
        activityLevelButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                viewModel.activityLevel = when (checkedId) {
                    R.id.button_activity_level_relaxed -> ActivityLevel.RELAXED
                    R.id.button_activity_level_seated -> ActivityLevel.SEATED
                    R.id.button_activity_level_light_exercise -> ActivityLevel.LIGHT_EXERCISE
                    R.id.button_activity_level_heavy_exercise -> ActivityLevel.HEAVY_EXERCISE
                    else -> error("Illegal button clicked")
                }
            }
        }
        measurementDurationSlider.apply {
            hasTickMarks = true
            progress = MeasureSettingsViewModel.DURATIONS.indexOf(viewModel.measurementDuration)
            max = MeasureSettingsViewModel.DURATIONS.lastIndex
            setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        viewModel.measurementDuration = MeasureSettingsViewModel.DURATIONS[progress]
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
                    override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
                },
            )
        }
        startButton.setOnClickListener {
            Intent(requireActivity(), MeasureActivity::class.java).apply {
                putExtra(MeasureActivity.EXTRA_MEASUREMENT_TYPE, viewModel.measurementType as Parcelable)
                putExtra(MeasureActivity.EXTRA_ACTIVITY_LEVEL, viewModel.activityLevel as Parcelable)
                putExtra(MeasureActivity.EXTRA_MEASUREMENT_DURATION, viewModel.measurementDuration)
                startActivity(this)
            }
        }
    }

    private fun updateMeasurementDescription() {
        measurementTypeDescription.text = when (viewModel.measurementType) {
            MeasurementType.FACE -> getString(R.string.measurement_type_description_face)
            MeasurementType.FINGER -> getString(R.string.measurement_type_description_finger)
        }
    }
}