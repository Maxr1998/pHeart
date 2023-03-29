package edu.uaux.pheart

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import edu.uaux.pheart.all_results.AllResultsFragment
import edu.uaux.pheart.measure.MeasureSettingsFragment
import edu.uaux.pheart.preferences.PreferencesFragment
import edu.uaux.pheart.profile.ProfileFragment
import edu.uaux.pheart.statistics.StatisticsFragment
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity(), OnItemSelectedListener {

    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView = findViewById(R.id.bottom_navigation)
        navigationView.setOnItemSelectedListener(this)

        replaceFragment<StatisticsFragment>()
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
}