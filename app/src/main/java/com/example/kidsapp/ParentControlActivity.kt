package com.example.kidsapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.util.*
import com.google.firebase.auth.FirebaseAuth


class ParentControlActivity : AppCompatActivity() {

    private lateinit var timerPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_parent_control)

        timerPrefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE)

        setupLanguageSpinner()
        setupTimerControls()
        setupReportButtons()
        setupOtherControls()
        setupLogoutButton()
    }

    private fun setupReportButtons() {
        findViewById<MaterialButton>(R.id.openWatchHistoryButton).setOnClickListener {
            startActivity(Intent(this, WatchHistoryActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.openWeeklyReportButton).setOnClickListener {
            startActivity(Intent(this, WeeklyReportActivity::class.java))
        }
    }

    private fun setupLanguageSpinner() {
        val languageSpinner = findViewById<Spinner>(R.id.languageSpinner)
        val languages = resources.getStringArray(R.array.languages_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        val currentLang = getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .getString("My_Lang", "ar")

        val selectedIndex = when (currentLang) {
            "en" -> 0
            "ar" -> 1
            "fr" -> 2
            else -> 1
        }
        languageSpinner.setSelection(selectedIndex)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedLangCode = when (position) {
                    0 -> "en"
                    1 -> "ar"
                    2 -> "fr"
                    else -> "ar"
                }

                val currentLangCode = getSharedPreferences("Settings", Context.MODE_PRIVATE)
                    .getString("My_Lang", "")

                if (selectedLangCode != currentLangCode) {
                    setLocale(selectedLangCode)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTimerControls() {
        val timerSpinner = findViewById<Spinner>(R.id.timerSpinner)
        val saveTimerButton = findViewById<MaterialButton>(R.id.saveTimerButton)

        val timerOptions = arrayOf(
            getString(R.string.timer_disabled),
            getString(R.string.timer_10_min),
            getString(R.string.timer_20_min),
            getString(R.string.timer_30_min),
            getString(R.string.timer_1_hour)
        )
        val timerMinutes = arrayOf(0, 10, 20, 30, 60)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timerOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timerSpinner.adapter = spinnerAdapter

        val savedMinutes = timerPrefs.getInt("screen_time", 0)
        val savedIndex = timerMinutes.indexOf(savedMinutes)
        timerSpinner.setSelection(if (savedIndex != -1) savedIndex else 0)

        saveTimerButton.setOnClickListener {
            val selectedIndex = timerSpinner.selectedItemPosition
            val selectedMinutes = timerMinutes[selectedIndex]

            timerPrefs.edit().putInt("screen_time", selectedMinutes).apply()

            Toast.makeText(
                this,
                getString(R.string.timer_saved_message, selectedMinutes),
                Toast.LENGTH_SHORT
            ).show()

            setupScreenTimeLimit(selectedMinutes)
            // زر تسجيل الخروج
            val logoutButton = findViewById<MaterialButton>(R.id.logoutButton)
            logoutButton.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

        }
    }

    private fun setupScreenTimeLimit(minutes: Int) {
        if (minutes > 0) {
            // تفعيل المؤقت
        } else {
            // تعطيل المؤقت
        }
    }

    private fun setupOtherControls() {
        val contentAgeGroup = findViewById<RadioGroup>(R.id.contentAgeGroup)
        val prefs = getSharedPreferences("ContentPrefs", MODE_PRIVATE)
        val savedAgeGroup = prefs.getString("age_group", "younger")

        when (savedAgeGroup) {
            "younger" -> contentAgeGroup.check(R.id.youngerOption)
            "older" -> contentAgeGroup.check(R.id.olderOption)
        }

        contentAgeGroup.setOnCheckedChangeListener { _, checkedId ->
            val ageGroup = when (checkedId) {
                R.id.youngerOption -> "younger"
                else -> "older"
            }
            prefs.edit().putString("age_group", ageGroup).apply()
        }

        val searchSwitch = findViewById<Switch>(R.id.searchSwitch)
        searchSwitch.isChecked = prefs.getBoolean("safe_search", true)
        searchSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("safe_search", isChecked).apply()
        }
    }

    private fun setupLogoutButton() {
        val logoutButton = findViewById<MaterialButton>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", langCode)
        editor.apply()
    }

    private fun loadLocale() {
        val prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = prefs.getString("My_Lang", "ar")
        language?.let { setLocale(it) }
    }
}
