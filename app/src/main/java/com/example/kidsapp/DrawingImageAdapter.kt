package com.example.kidsapp

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class DrawingImageAdapter(
    private val context: Context,
    private val imageResIds: List<Int>
) : BaseAdapter() {

    override fun getCount(): Int = imageResIds.size

    override fun getItem(position: Int): Any = imageResIds[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = convertView as? ImageView ?: ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(300, 300) // حجم الصورة داخل الشبكة
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(16, 16, 16, 16)
        }

        imageView.setImageResource(imageResIds[position])
        return imageView
    }
}
