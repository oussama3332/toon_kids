package com.example.kidsapp

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.kidsapp.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resourceIds = intent.getIntegerArrayListExtra("video_resource_ids") ?: return
        val startIndex = intent.getIntExtra("start_index", 0)

        val videoUris = resourceIds.map { resId ->
            Uri.parse("android.resource://$packageName/$resId")
        }.toMutableList() // ✅ التحويل إلى MutableList

        val adapter = VideoPagerAdapter(this, videoUris)
        binding.viewPager.adapter = adapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        binding.viewPager.setCurrentItem(startIndex, false)
    }
}
