package edu.uaux.pheart

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import edu.uaux.pheart.all_results.AllResultsFragment
import edu.uaux.pheart.all_results.MeasurementDummyRepository
import edu.uaux.pheart.database.AppDatabase
import edu.uaux.pheart.measure.MeasureSettingsFragment
import edu.uaux.pheart.preferences.PreferencesFragment
import edu.uaux.pheart.profile.ProfileFragment
import edu.uaux.pheart.statistics.StatisticsFragment
import edu.uaux.pheart.util.NotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.core.component.KoinComponent

class MainActivity : AppCompatActivity(), OnItemSelectedListener, KoinComponent {

    companion object {
        const val EXTRA_START_FRAGMENT = "edu.uaux.pheart.measure.EXTRA_SHOW_FRAGMENT"
    }

    private lateinit var navigationView: BottomNavigationView
    private val notificationService: NotificationService = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationService.createNotificationChannel()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                resetDb()
            }
        }

        navigationView = findViewById(R.id.bottom_navigation)
        navigationView.setOnItemSelectedListener(this)

        if (savedInstanceState == null) {
            val fragmentToStart = intent.extras?.getInt(EXTRA_START_FRAGMENT, 0) ?: 0
            if (fragmentToStart != 0) {
                navigationView.selectedItemId = fragmentToStart
            } else {
                replaceFragment<StatisticsFragment>()
            }
        }
    }

    private inline fun <reified T : Fragment> replaceFragment() {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, get<T>())
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.screen_home -> {
            replaceFragment<StatisticsFragment>()
            true
        }
        R.id.screen_measurements -> {
            replaceFragment<AllResultsFragment>()
            true
        }
        R.id.screen_add_measurement -> {
            replaceFragment<MeasureSettingsFragment>()
            true
        }
        R.id.screen_profile -> {
            replaceFragment<ProfileFragment>()
            true
        }
        R.id.screen_preferences -> {
            replaceFragment<PreferencesFragment>()
            true
        }
        else -> false
    }

    private fun resetDb() {
        val db = get<AppDatabase>()
        db.measurementDao().deleteAll()
        db.measurementDao().insertAll(MeasurementDummyRepository.generateMeasurements(200))
    }
}