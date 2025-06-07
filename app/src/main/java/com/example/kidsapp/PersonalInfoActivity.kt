package com.example.kidsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kidsapp.databinding.ActivityPersonalInfoBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class PersonalInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // إعداد Spinner الأعمار
        val ageRanges = listOf("3-5 سنوات", "6-8 سنوات", "9-12 سنوات")
        val ageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageRanges)
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = ageAdapter

        // إعداد Spinner صلة القرابة
        val guardianOptions = listOf("أب", "أم", "أخ", "أخت", "غير ذلك")
        val guardianAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, guardianOptions)
        guardianAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.guardianRelationSpinner.adapter = guardianAdapter

        // عند اختيار أي صلة وصي نعرض الحقول
        binding.guardianRelationSpinner.setOnItemSelectedListener { _, _, _, _ ->
            binding.guardianPhoneEditText.visibility = View.VISIBLE
            binding.guardianEmailEditText.visibility = View.VISIBLE
        }

        binding.nextToVideosButton.setOnClickListener {
            val name = binding.fullNameEditText.text.toString().trim()
            val age = binding.ageSpinner.selectedItem?.toString() ?: ""
            val gender = when (binding.genderRadioGroup.checkedRadioButtonId) {
                R.id.maleRadio -> "ذكر"
                R.id.femaleRadio -> "أنثى"
                else -> null
            }
            val guardianRelation = binding.guardianRelationSpinner.selectedItem?.toString() ?: ""
            val guardianPhone = binding.guardianPhoneEditText.text.toString().trim()
            val guardianEmail = binding.guardianEmailEditText.text.toString().trim()

            if (name.isEmpty() || gender == null || guardianPhone.isEmpty() || guardianEmail.isEmpty()) {
                Toast.makeText(this, "املأ جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val childId = UUID.randomUUID().toString()
            val childInfo = mapOf(
                "name" to name,
                "age" to age,
                "gender" to gender,
                "guardianRelation" to guardianRelation,
                "guardianPhone" to guardianPhone,
                "guardianEmail" to guardianEmail
            )

            FirebaseDatabase.getInstance().reference
                .child("children").child(childId).setValue(childInfo)
                .addOnSuccessListener {
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.putExtra("childName", name)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "حدث خطأ أثناء حفظ البيانات", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // لتسهيل الاستماع لـ Spinner
    private fun android.widget.Spinner.setOnItemSelectedListener(onItemSelected: (parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                onItemSelected(parent, view, position, id)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }
}
