package edu.uaux.pheart.measure

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.lifecycleScope
import edu.uaux.pheart.R
import edu.uaux.pheart.database.ActivityLevel
import edu.uaux.pheart.database.Measurement
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

class MeasureResultsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MEASUREMENT = "edu.uaux.pheart.measure.EXTRA_MEASUREMENT"
    }

    private val viewModel: MeasureResultsViewModel by viewModels()

    private lateinit var bpmTextView: TextView
    private lateinit var measurementDateTextView: TextView
    private lateinit var comparedToYesterdayTextView: TextView
    private lateinit var comparedToLast7DaysTextView: TextView

    private lateinit var restingHeartRangeView: HeartRangeView
    private lateinit var exerciseHeartRangeView: HeartRangeView

    private lateinit var exerciseHeartRateTextView: TextView

    private lateinit var exerciseGroup: Group
    private lateinit var restingGroup: Group

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val dateFormat = DateTimeFormatter.ofPattern("dd. MMM", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure_results)

        // Common
        bpmTextView = findViewById<ConstraintLayout>(R.id.measured_heart_rate).findViewById(R.id.bpm_text)
        measurementDateTextView = findViewById(R.id.measurement_date_text)

        // Resting
        comparedToYesterdayTextView = findViewById(R.id.compare_yesterday_average_value)
        comparedToLast7DaysTextView = findViewById(R.id.compare_last_week_average_value)
        restingHeartRangeView = findViewById(R.id.resting_rating_graph)
        restingGroup = findViewById(R.id.resting_view_group)


        // Exercising
        exerciseHeartRangeView = findViewById(R.id.exercise_heartrate_graph)
        exerciseHeartRateTextView = findViewById<ViewGroup>(R.id.exercise_heart_rate).findViewById(R.id.bpm_text)
        exerciseGroup = findViewById(R.id.exercise_view_group)


        val measurement = intent.extras?.getParcelable<Measurement>(EXTRA_MEASUREMENT)

        if (measurement != null) {
            with(measurement) {
                bpmTextView.text = bpm.toString()
                measurementDateTextView.text = "${dateFormat.format(timestamp)}, ${timeFormat.format(timestamp)}"
                restingHeartRangeView.currentValue = bpm
                exerciseHeartRangeView.currentValue = bpm


                when (activityLevel) {
                    ActivityLevel.RELAXING -> {
                        exerciseGroup.visibility = View.GONE
                        restingGroup.visibility = View.VISIBLE
                    }
                    ActivityLevel.EXERCISING -> {
                        exerciseGroup.visibility = View.VISIBLE
                        restingGroup.visibility = View.GONE
                    }
                }
            }

            lifecycleScope.launch {
                viewModel.readMeasurementResults(measurement)
            }
        }

        viewModel.comparedToYesterday.observe(this) { comparedToYesterday ->
            comparedToYesterdayTextView.text =
                if (comparedToYesterday != null) "%+d".format(comparedToYesterday) else getString(
                    R.string.compare_to_average_no_value,
                )
        }

        viewModel.comparedToLast7Days.observe(this) { comparedToLast7Days ->
            comparedToLast7DaysTextView.text =
                if (comparedToLast7Days != null) "%+d".format(comparedToLast7Days) else getString(
                    R.string.compare_to_average_no_value,
                )
        }

        viewModel.restingBpmGoodRange.observe(this) { bpmGoodRange ->
            restingHeartRangeView.goodStart = bpmGoodRange.first
            restingHeartRangeView.goodEnd = bpmGoodRange.last
        }

        viewModel.exercisingBpmGoodRange.observe(this) { bpmGoodRange ->
            exerciseHeartRangeView.goodStart = bpmGoodRange.first
            exerciseHeartRangeView.goodEnd = bpmGoodRange.last

            exerciseHeartRateTextView.text = "${bpmGoodRange.first} - ${bpmGoodRange.last}"
        }
    }
}