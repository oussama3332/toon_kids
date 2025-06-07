package com.example.kidsapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View // أضف هذا الاستيراد
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException // أضف هذه
import com.google.firebase.auth.FirebaseAuthWeakPasswordException // أضف هذه
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException // أضف هذه
import com.example.kidsapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            // التحقق من صحة البيانات
            if (!isValidEmail(email)) {
                binding.emailEditText.error = "البريد الإلكتروني غير صالح"
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                binding.passwordEditText.error = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.confirmPasswordEditText.error = "كلمة المرور غير متطابقة"
                return@setOnClickListener
            }

            // عرض مؤشر التحميل (تأكد أن لديك ProgressBar في ملف التخطيط)
            binding.progressBar.visibility = View.VISIBLE
            binding.signupButton.isEnabled = false

            // إنشاء الحساب في Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.progressBar.visibility = View.GONE
                    binding.signupButton.isEnabled = true

                    if (task.isSuccessful) {
                        // إرسال بريد التحقق
                        sendEmailVerification()
                        Toast.makeText(this, "تم إنشاء الحساب بنجاح!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        showSignupError(task.exception)
                    }
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "تم إرسال بريد التحقق", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "فشل إرسال بريد التحقق: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showSignupError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthUserCollisionException -> "هذا البريد الإلكتروني مسجل مسبقاً"
            is FirebaseAuthWeakPasswordException -> "كلمة المرور ضعيفة جداً"
            is FirebaseAuthInvalidCredentialsException -> "صيغة البريد الإلكتروني غير صالحة"
            else -> "خطأ في التسجيل: ${exception?.message}"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}