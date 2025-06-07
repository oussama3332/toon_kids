package com.example.kidsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.kidsapp.databinding.ActivityVideoFeedBinding

class VideoFeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoFeedBinding
    private lateinit var videoAdapter: VideoPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
    }

    private fun setupViewPager() {
        val videoList = listOf(
            Uri.parse("android.resource://$packageName/${R.raw.video1}"),
            Uri.parse("android.resource://$packageName/${R.raw.video2}")
        )

        videoAdapter = VideoPagerAdapter(this, videoList)

        binding.viewPager.apply {
            adapter = videoAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
            offscreenPageLimit = 3

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    videoAdapter.playVideo(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager2.SCROLL_STATE_DRAGGING -> videoAdapter.pauseCurrentVideo()
                        ViewPager2.SCROLL_STATE_IDLE -> videoAdapter.playVideo(binding.viewPager.currentItem)
                    }
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        videoAdapter.pauseCurrentVideo()
    }

    override fun onResume() {
        super.onResume()
        videoAdapter.playVideo(binding.viewPager.currentItem)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoAdapter.releaseAllPlayers()
    }
}