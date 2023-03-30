package edu.uaux.pheart.measure

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import edu.uaux.pheart.R
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

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val dateFormat = DateTimeFormatter.ofPattern("dd. MMM", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure_results)

        bpmTextView = findViewById<ConstraintLayout>(R.id.measured_heart_rate).findViewById(R.id.bpm_text)
        measurementDateTextView = findViewById(R.id.measurement_date_text)
        comparedToYesterdayTextView = findViewById(R.id.compare_yesterday_average_value)
        comparedToLast7DaysTextView = findViewById(R.id.compare_last_week_average_value)

        val measurement = intent.extras?.getParcelable<Measurement>(EXTRA_MEASUREMENT)

        if (measurement != null) {
            with(measurement) {
                bpmTextView.text = bpm.toString()
                measurementDateTextView.text = "${dateFormat.format(timestamp)}, ${timeFormat.format(timestamp)}"
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
    }
}