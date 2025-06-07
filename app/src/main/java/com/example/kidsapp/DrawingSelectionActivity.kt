package com.example.kidsapp

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity

class DrawingSelectionActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var imageAdapter: DrawingImageAdapter

    // قائمة الصور المتوفرة في مجلد drawable
    private val imageResIds = listOf(
        R.drawable.animal_lion,
        R.drawable.animal_cat,
        R.drawable.animal_elephant,
        R.drawable.animal_dog,
        R.drawable.animal_lion,
        R.drawable.animal_bird
        // أضف المزيد حسب ما تضعه في مجلد drawable
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing_selection)

        gridView = findViewById(R.id.drawingGrid)
        imageAdapter = DrawingImageAdapter(this, imageResIds)
        gridView.adapter = imageAdapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedImageResId = imageResIds[position]
            val intent = Intent(this, DrawingActivity::class.java)
            intent.putExtra("selectedImageResId", selectedImageResId)
            startActivity(intent)
        }
    }
}
