package com.example.kidsapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView

class ParentalGateActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parental_gate)

        sharedPrefs = getSharedPreferences("ParentPrefs", MODE_PRIVATE)
        val pinEditText = findViewById<TextInputEditText>(R.id.pinEditText)
        val enterButton = findViewById<MaterialButton>(R.id.enterPinButton)
        val forgotPinTextView = findViewById<TextView>(R.id.forgotPinTextView)

        enterButton.setOnClickListener {
            val inputPin = pinEditText.text.toString()
            val savedPin = sharedPrefs.getString("parent_pin", "1234") // Default first time

            if (inputPin == savedPin) {
                startActivity(Intent(this, ParentControlActivity::class.java))
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } else {
                Toast.makeText(this, "رمز غير صحيح ❌", Toast.LENGTH_SHORT).show()
                pinEditText.error = "الرجاء إدخال الرمز الصحيح"
                pinEditText.text?.clear()
            }
        }

        forgotPinTextView.setOnClickListener {
            val email = sharedPrefs.getString("user_email", null)

            if (email != null) {
                // Generate temporary PIN (in real app, send via email)
                val tempPin = generateRandomPin()
                sharedPrefs.edit().putString("parent_pin", tempPin).apply()

                Toast.makeText(
                    this,
                    "تم إرسال رمز مؤقت إلى: ${obfuscateEmail(email)}\nالرمز: $tempPin",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "الرجاء تسجيل بريد إلكتروني في إعدادات الوالدين أولاً",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun generateRandomPin(): String {
        return (1000..9999).random().toString()
    }

    private fun obfuscateEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email
        val name = parts[0]
        val domain = parts[1]
        return "${name.take(2)}***@$domain"
    }
}