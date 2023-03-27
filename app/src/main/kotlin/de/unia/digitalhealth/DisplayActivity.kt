package de.unia.digitalhealth

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DisplayActivity : AppCompatActivity() {

    private lateinit var displayText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)
        displayText = findViewById(R.id.display_text)

        val text = intent.extras?.getString("text").orEmpty()
        displayText.text = text
        displayText.isSelected = true
    }
}