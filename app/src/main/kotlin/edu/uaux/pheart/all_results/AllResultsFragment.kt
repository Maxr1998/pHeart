package edu.uaux.pheart.all_results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uaux.pheart.R

class AllResultsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.all_results_recycler)
        val adapter = MeasurementAdapter()
        val layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        adapter.measurements = MeasurementDummyRepository.allMeasurementsDescending
    }
}