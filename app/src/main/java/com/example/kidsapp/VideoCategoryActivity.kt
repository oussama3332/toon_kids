package com.example.kidsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kidsapp.databinding.ActivityVideoCategoryBinding

class VideoCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getStringExtra("category") ?: return

        binding.categoryTitle.text = when (category) {
            "letters" -> "تعلم الحروف"
            "numbers" -> "تعلم الأرقام"
            "songs" -> "الأناشيد"
            else -> "الفيديوهات"
        }

        // قائمة فيديوهات محلية حسب التصنيف
        val videos = when (category) {
            "letters" -> listOf(
                VideoItem("حرف الألف", R.raw.letter_alif),
                VideoItem("حرف الباء", R.raw.letter_ba)
            )
            "numbers" -> listOf(
                VideoItem("رقم واحد", R.raw.number_one),
                VideoItem("رقم اثنان", R.raw.number_two)
            )
            "songs" -> listOf(
                VideoItem("نشيد الحروف", R.raw.song_letters)
            )
            else -> emptyList()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = VideoListAdapter(videos) { selectedVideo ->
            val videoResourceIds = videos.map { it.resourceId }
            val startIndex = videoResourceIds.indexOf(selectedVideo.resourceId)

            val intent = Intent(this, VideoActivity::class.java).apply {
                putIntegerArrayListExtra("video_resource_ids", ArrayList(videoResourceIds))
                putExtra("start_index", startIndex)
            }
            startActivity(intent)
        }
    }
}
