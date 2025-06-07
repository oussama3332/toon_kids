package com.example.kidsapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TimeUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_up)

        findViewById<Button>(R.id.okButton).setOnClickListener {
            finishAffinity() // Close the entire app
        }
    }
}