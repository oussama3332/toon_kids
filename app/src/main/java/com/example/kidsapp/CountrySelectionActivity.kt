package com.example.kidsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CountrySelectionActivity : AppCompatActivity() {

    private var selectedCountry: String? = null
    private lateinit var nextButton: View
    private var otherCountriesRecycler: RecyclerView? = null
    private var isOtherVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_selection)

        nextButton = findViewById(R.id.nextButton)
        otherCountriesRecycler = findViewById(R.id.otherCountriesRecycler)
        otherCountriesRecycler?.layoutManager = LinearLayoutManager(this)
        otherCountriesRecycler?.setHasFixedSize(true)

        val otherCountries = listOf(
            "قطر", "الأردن", "فلسطين", "عُمان",
            "الكويت", "البحرين", "اليمن", "سوريا"
        )

        val adapter = OtherCountriesAdapter(otherCountries) { country ->
            selectedCountry = country
            Toast.makeText(this, "تم اختيار: $country", Toast.LENGTH_SHORT).show()
        }

        otherCountriesRecycler?.adapter = adapter

        // ربط كل مستطيل دولة بزر الضغط واختيار الدولة
        val countryViews = listOf(
            R.id.countryMorocco to "المغرب",
            R.id.countryEgypt to "مصر",
            R.id.countryAlgeria to "الجزائر",
            R.id.countrySaudi to "السعودية",
            R.id.countryUAE to "الإمارات",
            R.id.countryLebanon to "لبنان"  // لبنان تم إضافته كمستطيل في XML
        )

        countryViews.forEach { (viewId, countryName) ->
            findViewById<LinearLayout>(viewId).setOnClickListener {
                selectedCountry = countryName
                Toast.makeText(this, "تم اختيار: $countryName", Toast.LENGTH_SHORT).show()
            }
        }

        // زر التالي
        nextButton.setOnClickListener {
            if (selectedCountry != null) {
                val intent = Intent(this, PersonalInfoActivity::class.java)
                intent.putExtra("country", selectedCountry)
                startActivity(intent)
            } else {
                Toast.makeText(this, "يرجى اختيار الدولة أولاً", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // إظهار/إخفاء قائمة الدول الأخرى
    fun showOtherCountries(view: View?) {
        isOtherVisible = !isOtherVisible
        otherCountriesRecycler?.visibility = if (isOtherVisible) View.VISIBLE else View.GONE
    }
}
