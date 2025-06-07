package com.example.kidsapp

import android.os.Bundle
import android.widget.ListView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class WeeklyReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_report)

        val listView = findViewById<ListView>(R.id.weekReportList)
        val prefs = getSharedPreferences("WeeklyReport", MODE_PRIVATE)

        val daysOfWeek = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")

        val reportList = mutableListOf<String>()

        for (i in 1..7) {
            val data = prefs.getString("day_$i", "0,0") ?: "0,0"
            val parts = data.split(",")
            val videoCount = parts[0]
            val minutes = parts[1]

            val dayName = daysOfWeek[(i - 1) % 7]
            reportList.add("$dayName: 🎞️ $videoCount فيديو - ⏱️ $minutes دقيقة")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reportList)
        listView.adapter = adapter
    }
}
