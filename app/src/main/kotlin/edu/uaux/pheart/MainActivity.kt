package edu.uaux.pheart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.edit_text)
        button = findViewById(R.id.button)

        button.setOnClickListener {
            val currentText = editText.text.toString()
            Intent(this, DisplayActivity::class.java).apply {
                putExtra("text", currentText)
                startActivity(this)
            }
        }
    }
}