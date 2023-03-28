package edu.uaux.pheart

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.uaux.pheart.all_results.AllResultsFragment
import edu.uaux.pheart.measure.MeasureActivity
import edu.uaux.pheart.preferences.PreferencesFragment
import edu.uaux.pheart.profile.ProfileFragment
import edu.uaux.pheart.statistics.StatisticsFragment
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity() {

    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView = findViewById(R.id.bottom_navigation)

        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.screen_home -> {
                    replaceFragment<StatisticsFragment>()
                    true
                }
                R.id.screen_measurements -> {
                    replaceFragment<AllResultsFragment>()
                    true
                }
                R.id.screen_add_measurement -> {
                    startActivity(Intent(this, MeasureActivity::class.java))
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
    }

    private inline fun <reified T : Fragment> replaceFragment() {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, get<T>())
        }
    }
}