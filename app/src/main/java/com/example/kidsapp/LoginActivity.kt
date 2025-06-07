package com.example.kidsapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kidsapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // تحقق من صحة البيانات
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditText.error = "البريد الإلكتروني غير صالح"
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.passwordEditText.error = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, CountrySelectionActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "خطأ في تسجيل الدخول: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.forgotPasswordText.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        val message = if (task.isSuccessful) {
                            "تم إرسال رابط إعادة التعيين إلى بريدك الإلكتروني"
                        } else {
                            "خطأ: ${task.exception?.message}"
                        }
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
            } else {
                binding.emailEditText.error = "أدخل البريد الإلكتروني أولاً"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, CountrySelectionActivity::class.java))
            finish()
        }
    }
}