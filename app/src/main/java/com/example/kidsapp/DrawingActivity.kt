package com.example.kidsapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Color


class DrawingActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var imageOriginal: ImageView
    private val STORAGE_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        drawingView = findViewById(R.id.drawingView)
        imageOriginal = findViewById(R.id.imageOriginal)

        val btnUndo: ImageButton = findViewById(R.id.btnUndo)
        val btnRedo: ImageButton = findViewById(R.id.btnRedo)
        val btnEraser: ImageButton = findViewById(R.id.btnEraser)
        val btnSave: ImageButton = findViewById(R.id.btnSave)
        val btnShare: ImageButton = findViewById(R.id.btnShare)
        val btnScreenshot: ImageButton = findViewById(R.id.btnScreenshot)
        val btnShowResult: ImageButton = findViewById(R.id.btnShowResult)
        val btnOriginalImage: ImageButton = findViewById(R.id.btnOriginalImage)

        btnUndo.setOnClickListener { drawingView.undo() }
        btnRedo.setOnClickListener { drawingView.redo() }
        btnEraser.setOnClickListener {
            drawingView.setColor(Color.WHITE)
        }

        btnSave.setOnClickListener {
            if (checkStoragePermission()) saveDrawingToFile() else requestStoragePermission()
        }

        btnShare.setOnClickListener {
            if (checkStoragePermission()) shareDrawing() else requestStoragePermission()
        }

        btnScreenshot.setOnClickListener {
            if (checkStoragePermission()) takeScreenshot() else requestStoragePermission()
        }

        btnOriginalImage.setOnClickListener {
            imageOriginal.visibility = if (imageOriginal.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // أضف الألوان إلى لوحة الألوان
        val colorIds = listOf(
            R.id.colorRed to "#F44336",
            R.id.colorOrange to "#FF9800",
            R.id.colorYellow to "#FFEB3B",
            R.id.colorGreen to "#4CAF50",
            R.id.colorBlue to "#2196F3",
            R.id.colorPurple to "#9C27B0"
        )

        colorIds.forEach { (id, colorStr) ->
            findViewById<View>(id).setOnClickListener {
                drawingView.setColor(Color.parseColor(colorStr))
            }
        }

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_drawing)
        imageOriginal.setImageBitmap(bitmap)
    }

    private fun saveDrawingToFile() {
        val bitmap = drawingView.getBitmap()
        val file = File(getExternalFilesDir(null), "drawing_${System.currentTimeMillis()}.png")
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, "تم حفظ الرسم", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "فشل الحفظ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareDrawing() {
        val bitmap = drawingView.getBitmap()
        val file = File(getExternalFilesDir(null), "share_${System.currentTimeMillis()}.png")
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()

            val uri: Uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "مشاركة الرسم"))
        } catch (e: IOException) {
            Toast.makeText(this, "فشل المشاركة: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun takeScreenshot() {
        val view = window.decorView.rootView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val file = File(getExternalFilesDir(null), "screenshot_${System.currentTimeMillis()}.png")
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, "تم التقاط لقطة شاشة", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "فشل الالتقاط: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }
}
