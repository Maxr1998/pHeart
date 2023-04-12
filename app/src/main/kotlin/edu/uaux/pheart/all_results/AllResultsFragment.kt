package edu.uaux.pheart.all_results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import edu.uaux.pheart.R

/**
 * Fragment that displays all the measurements.
 */
class AllResultsFragment : Fragment() {

    private val viewModel: AllResultsViewModel by viewModels()

    private val measurementAdapter = MeasurementAdapter()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.measurements.observe(this) { measurements ->
            measurementAdapter.measurements = measurements
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.all_results_recycler)
        recyclerView.adapter = measurementAdapter
    }
}