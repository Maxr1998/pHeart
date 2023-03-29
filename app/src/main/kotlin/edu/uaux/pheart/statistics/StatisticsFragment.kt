package edu.uaux.pheart.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.uaux.pheart.R
import java.time.format.DateTimeFormatter
import java.util.Locale

class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels()

    private lateinit var bpmTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var dateTextView: TextView

    private lateinit var dailyAverageTextView: TextView
    private lateinit var dailyMinimumTextView: TextView
    private lateinit var dailyMaximumTextView: TextView

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val dateFormat = DateTimeFormatter.ofPattern("dd. MMMM", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.dailyMeasurements.observe(this) { measurements ->
            if (measurements.isEmpty()) {
                dailyAverageTextView.text = "-- bpm"
                dailyMinimumTextView.text = "-- bpm"
                dailyMaximumTextView.text = "-- bpm"
                return@observe
            }
            val average = measurements.sumOf { it.bpm } / measurements.size
            dailyAverageTextView.text = "$average bpm"
            dailyMinimumTextView.text = "${measurements.minOf { it.bpm }} bpm"
            dailyMaximumTextView.text = "${measurements.maxOf { it.bpm }} bpm"
        }

        viewModel.selectedMeasurement.observe(this) { selected ->
            if (selected == null) {
                bpmTextView.text = "-- bpm"
                timeTextView.text = "--:--"
                return@observe
            }
            bpmTextView.text = "${selected.bpm} bpm"
            timeTextView.text = timeFormat.format(selected.timestamp)
        }

        viewModel.dayInstant.observe(this) { day ->
            if (day == null) {
                dateTextView.text = "-"
                return@observe
            }
            dateTextView.text = day.format(dateFormat)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bpmTextView = view.findViewById(R.id.bpm_text)
        timeTextView = view.findViewById(R.id.time_text)
        dateTextView = view.findViewById(R.id.date_text)

        dailyAverageTextView = view.findViewById(R.id.daily_average_bpm_text)
        dailyMinimumTextView = view.findViewById(R.id.daily_minimum_bpm_text)
        dailyMaximumTextView = view.findViewById(R.id.daily_maximum_bpm_text)

        viewModel.loadToday()
    }
}