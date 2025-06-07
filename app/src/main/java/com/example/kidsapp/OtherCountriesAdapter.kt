package com.example.kidsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OtherCountriesAdapter(
    private val countries: List<String>,
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<OtherCountriesAdapter.CountryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]

        // تعيين اسم الدولة
        holder.countryName.text = country

        // تعيين علم الدولة (هذه الدالة تحتاج للتطبيق حسب ملفات الأعلام لديك)
        setCountryFlag(holder.flagImageView, country)

        // التعامل مع النقر
        holder.itemView.setOnClickListener { listener(country) }
    }

    override fun getItemCount(): Int = countries.size

    private fun setCountryFlag(imageView: ImageView, countryName: String) {
        val flagResId = when (countryName) {
            "قطر" -> R.drawable.qatar_flag
            "الأردن" -> R.drawable.jordan_flag
            "فلسطين" -> R.drawable.flag_palestine
            "لبنان" -> R.drawable.lebanon_flag
            "عُمان" -> R.drawable.flag_oman
            "الكويت" -> R.drawable.flag_kuwait
            "البحرين" -> R.drawable.flag_bahrain
            "اليمن" -> R.drawable.flag_yemen
            "سوريا" -> R.drawable.flag_syria
            // يمكنك إضافة المزيد من الدول هنا
            else -> R.drawable.default_flag // صورة افتراضية
        }
        imageView.setImageResource(flagResId)
    }

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flagImageView: ImageView = itemView.findViewById(R.id.flagImageView)
        val countryName: TextView = itemView.findViewById(R.id.countryNameTextView)
    }
}