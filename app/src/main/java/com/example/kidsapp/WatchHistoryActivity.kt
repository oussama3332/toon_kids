package com.example.kidsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class WatchHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_history)

        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.historyRecyclerView)
        emptyView = findViewById(R.id.emptyHistoryText)

        setupToolbar()
        loadHistory()
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadHistory() {
        val prefs = getSharedPreferences("WatchHistory", MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<List<WatchHistoryItem>>() {}.type
        val historyJson = prefs.getString("history", null)
        val historyList = if (historyJson != null) {
            gson.fromJson<List<WatchHistoryItem>>(historyJson, type)
        } else {
            emptyList()
        }

        if (historyList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = HistoryAdapter(historyList.reversed()) { item ->
                // Handle item click
                val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                    putExtra("video_url", item.videoUrl)
                    putExtra("video_title", item.title)
                }
                startActivity(intent)
            }
        }
    }

    data class WatchHistoryItem(
        val title: String,
        val videoUrl: String,
        val thumbnailUrl: String? = null,
        val duration: String = "00:00",
        val timestamp: Long = System.currentTimeMillis()
    )

    class HistoryAdapter(
        private val items: List<WatchHistoryItem>,
        private val onItemClick: (WatchHistoryItem) -> Unit
    ) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

        class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.historyTitle)
            val date: TextView = view.findViewById(R.id.historyDate)
            val thumbnail: ImageView = view.findViewById(R.id.historyThumbnail)
            val duration: TextView = view.findViewById(R.id.historyDuration)
            val playButton: ImageView = view.findViewById(R.id.historyPlayButton)
            val shareButton: ImageView = view.findViewById(R.id.historyShareButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history, parent, false)
            return HistoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            val item = items[position]

            holder.title.text = item.title
            holder.duration.text = item.duration

            // Format date
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            holder.date.text = dateFormat.format(Date(item.timestamp))

            // Load thumbnail (placeholder if no thumbnail available)
            Glide.with(holder.itemView.context)
                .load(item.thumbnailUrl ?: R.drawable.video_placeholder)
                .into(holder.thumbnail)

            holder.playButton.setOnClickListener {
                onItemClick(item)
            }

            holder.shareButton.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "${item.title}\n${item.videoUrl}")
                }
                holder.itemView.context.startActivity(
                    Intent.createChooser(shareIntent, "مشاركة الفيديو")
                )
            }

            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        override fun getItemCount() = items.size
    }
}