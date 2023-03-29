package edu.uaux.pheart.measure

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import edu.uaux.pheart.R

class MeasureSettingsFragment : Fragment() {

    private lateinit var startButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_measure_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startButton = view.findViewById(R.id.button_start_measurement)
        startButton.setOnClickListener {
            startActivity(Intent(requireActivity(), MeasureActivity::class.java))
        }
    }
}