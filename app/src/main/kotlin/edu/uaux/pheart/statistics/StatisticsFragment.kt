package edu.uaux.pheart.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.button.MaterialButtonToggleGroup
import edu.uaux.pheart.R
import edu.uaux.pheart.util.avgOf
import edu.uaux.pheart.util.ext.resolveThemeColor
import edu.uaux.pheart.util.ext.toast
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels()

    private lateinit var buttonGroup: MaterialButtonToggleGroup
    private lateinit var dailyStatsButton: Button
    private lateinit var weeklyStatsButton: Button

    private lateinit var bpmTextView: TextView
    private lateinit var timeTextView: TextView

    private lateinit var barChart: BarChart

    private lateinit var dateTextView: TextView
    private lateinit var dailyAverageTextView: TextView
    private lateinit var dailyMinimumTextView: TextView
    private lateinit var dailyMaximumTextView: TextView

    private lateinit var previousDayButton: Button
    private lateinit var nextDayButton: Button

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val dateFormat = DateTimeFormatter.ofPattern("dd. MMMM", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.dailyMeasurements.observe(this) { measurements ->
            updateChartData()
            if (measurements.isEmpty()) {
                dailyAverageTextView.setText(R.string.no_value_indicator)
                dailyMinimumTextView.setText(R.string.no_value_indicator)
                dailyMaximumTextView.setText(R.string.no_value_indicator)
                return@observe
            }
            dailyAverageTextView.text = measurements.avgOf { it.bpm }.toString()
            dailyMinimumTextView.text = measurements.minOf { it.bpm }.toString()
            dailyMaximumTextView.text = measurements.maxOf { it.bpm }.toString()
        }

        viewModel.selectedMeasurement.observe(this) { selected ->
            if (selected == null) {
                bpmTextView.setText(R.string.no_value_indicator)
                timeTextView.setText(R.string.no_value_indicator)
                return@observe
            }
            bpmTextView.text = selected.avgBpm.toString()
            timeTextView.text = selected.timeOfDay.toFormattedString(includeSeconds = false)
        }

        viewModel.dayInstant.observe(this) { day ->
            if (day == null) {
                dateTextView.setText(R.string.no_value_indicator)
                nextDayButton.isEnabled = false
                previousDayButton.isEnabled = false
                return@observe
            }
            dateTextView.text = day.format(dateFormat)

            val now = ZonedDateTime.now(ZoneId.systemDefault())
            nextDayButton.isEnabled = day.isBefore(now.startOfDay())
            previousDayButton.isEnabled = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barChart = view.findViewById(R.id.graph)

        buttonGroup = view.findViewById(R.id.btn_group)
        dailyStatsButton = view.findViewById(R.id.btn_day)
        weeklyStatsButton = view.findViewById(R.id.btn_week)

        bpmTextView = view.findViewById<ConstraintLayout>(R.id.selected_heart_rate).findViewById(R.id.bpm_text)
        timeTextView = view.findViewById(R.id.time_text)
        dateTextView = view.findViewById(R.id.date_text)

        dailyAverageTextView =
            view.findViewById<ConstraintLayout>(R.id.average_bpm_container).findViewById(R.id.bpm_text)
        dailyMinimumTextView =
            view.findViewById<ConstraintLayout>(R.id.minimum_bpm_container).findViewById(R.id.bpm_text)
        dailyMaximumTextView =
            view.findViewById<ConstraintLayout>(R.id.maximum_bpm_container).findViewById(R.id.bpm_text)

        previousDayButton = view.findViewById(R.id.btn_prev)
        nextDayButton = view.findViewById(R.id.btn_next)

        previousDayButton.setOnClickListener {
            viewModel.toPreviousDay()
        }

        nextDayButton.setOnClickListener {
            viewModel.toNextDay()
        }

        buttonGroup.addOnButtonCheckedListener { _, button, isChecked ->
            if (isChecked) {
                when (button) {
                    R.id.btn_day -> {
                        viewModel.switchTo(StatisticsFragmentMode.DAILY)
                        requireActivity().toast("daily stats")
                    }
                    R.id.btn_week -> {
                        viewModel.switchTo(StatisticsFragmentMode.WEEKLY)
                        requireActivity().toast("weekly stats")
                    }
                }
            }
        }

        viewModel.switchTo(StatisticsFragmentMode.DAILY)
        viewModel.loadToday()
        styleChart()
        updateChartData()
    }

    private fun styleChart() {
        val textColor = requireContext().resolveThemeColor(R.attr.colorOnSurface)

        // style chart
        barChart.description = null
        barChart.setNoDataText("No measurements found.")
        barChart.setFitBars(true)
        barChart.setScaleEnabled(false) // disable zoom and pan
        barChart.legend.isEnabled = false
        barChart.setOnChartValueSelectedListener(viewModel.onChartValueSelectedListener)

        // style axis
        barChart.xAxis.valueFormatter = TimeAxisFormatter(timeFormat)
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.textColor = textColor

        barChart.axisLeft.isEnabled = false

        barChart.axisRight.axisMinimum = 0f
        barChart.axisRight.textColor = textColor
    }

    private fun updateChartData() {
        // aggregate the daily data over the course of the 24h in a day
        val hourlyAverageBpm = buildList {
            for (hour in 0 until 24) {
                add(0f)
            }
            viewModel.dailyMeasurements.value!!
                .groupBy { it.timestamp.hour }
                .forEach { (hour, measurements) -> this[hour] = measurements.avgOf { it.bpm }.toFloat() }
        }
        val entries = hourlyAverageBpm.mapIndexed { hour, average ->
            BarEntry(
                hour.toFloat(),
                average,
                StatisticsViewModel.AverageBpm(
                    TimeOfDay(hour, 0, 0),
                    avgBpm = average.toInt(),
                ),
            )
        }

        val dataset = BarDataSet(entries, "bpm")
        styleDataset(dataset)

        val barData = BarData(dataset)
        barData.setValueFormatter(NoBarLabelsFormatter())

        barChart.data = barData
        barChart.invalidate()
    }

    private fun styleDataset(barDataSet: BarDataSet) {
        barDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        barDataSet.color = requireContext().resolveThemeColor(R.attr.barChartColor)
        barDataSet.highLightColor = requireContext().resolveThemeColor(R.attr.barChartHighlightColor)
    }
}