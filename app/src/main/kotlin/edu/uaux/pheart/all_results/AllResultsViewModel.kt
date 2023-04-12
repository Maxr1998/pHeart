package edu.uaux.pheart.all_results

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import edu.uaux.pheart.database.AppDatabase
import edu.uaux.pheart.database.Measurement
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * The ViewModel for [AllResultsFragment].
 */
class AllResultsViewModel(app: Application) : AndroidViewModel(app), KoinComponent {
    private val database: AppDatabase = get()

    /**
     * All measurements in the database.
     */
    val measurements: LiveData<List<Measurement>> = database.measurementDao().getAll()
}