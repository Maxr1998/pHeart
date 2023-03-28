package edu.uaux.pheart.all_results

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import edu.uaux.pheart.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MeasurementAdapter() : Adapter<MeasurementAdapter.MeasurementViewHolder>() {

    var measurements = listOf<Measurement>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    class MeasurementViewHolder(view: View) : ViewHolder(view) {
        val bpmText: TextView = view.findViewById(R.id.bpm_text)
        val datetimeText: TextView = view.findViewById(R.id.datetime_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.measurement_item, parent, false)
        return MeasurementViewHolder(view)
    }

    override fun getItemCount(): Int = measurements.size

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val measurement = measurements[position]

        holder.bpmText.text = measurement.bpm.toString() + " bpm"
        holder.datetimeText.text = dateFormatter.format(Date.from(measurement.dateInstant))
    }
}