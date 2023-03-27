package edu.uaux.pheart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.uaux.pheart.preferences.PreferencesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView = findViewById(R.id.bottom_navigation)

        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.screen_home -> {
                    true
                }
                R.id.screen_measurements -> {
                    true
                }
                R.id.screen_add_measurement -> {
                    true
                }
                R.id.screen_profile -> {
                    true
                }
                R.id.screen_preferences -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, PreferencesFragment())
                    }
                    true
                }
                else -> false
            }
        }
    }
}