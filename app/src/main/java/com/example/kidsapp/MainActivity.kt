package com.example.kidsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Make sure this uses the XML with VideoView we created

        val videoView = findViewById<VideoView>(R.id.introVideoView)

        // Set video path (place your video in res/raw folder)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.kids_intro
        videoView.setVideoURI(Uri.parse(videoPath))

        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false // Play only once
            videoView.start()

            // Set a timeout in case video fails to play or complete
            Handler(Looper.getMainLooper()).postDelayed({
                navigateToLogin()
            }, 5000) // 5 seconds max wait time
        }

        videoView.setOnCompletionListener {
            // When video finishes, go to login screen
            navigateToLogin()
        }

        videoView.setOnErrorListener { _, _, _ ->
            // If video fails, still proceed after short delay
            navigateToLogin()
            true
        }
    }

    private fun navigateToLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 500) // Small delay after video ends
    }
}