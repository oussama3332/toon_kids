package com.example.kidsapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class VideoAdapter(
    private val videoUris: List<Uri>,
    private val playListener: VideoPlayListener? = null
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private val players = mutableListOf<SimpleExoPlayer>()
    private var currentPlayingPosition = -1

    interface VideoPlayListener {
        fun onVideoPlayed(videoTitle: String, videoUrl: String)
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerView: PlayerView = itemView.findViewById(R.id.playerView)
        val videoTitle: TextView = itemView.findViewById(R.id.videoTitle)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val shareButton: ImageView = itemView.findViewById(R.id.shareButton)
        val reportButton: ImageView = itemView.findViewById(R.id.reportButton)
        var player: SimpleExoPlayer? = null
        var isBlocked: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    private fun isBlockedVideo(context: Context, videoUrl: String): Boolean {
        val prefs = context.getSharedPreferences("BlockedPrefs", Context.MODE_PRIVATE)
        val blockedList = prefs.getStringSet("blocked_links", emptySet()) ?: emptySet()
        return blockedList.any { videoUrl.contains(it, ignoreCase = true) }
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val context = holder.itemView.context
        val videoUri = videoUris[position]
        val videoUrl = videoUri.toString()
        val videoTitle = "Video ${position + 1}"

        // Check if video is blocked
        holder.isBlocked = isBlockedVideo(context, videoUrl)

        if (holder.isBlocked) {
            holder.playerView.visibility = View.GONE
            holder.videoTitle.text = context.getString(R.string.blocked_video_title)
            holder.likeButton.visibility = View.GONE
            holder.shareButton.visibility = View.GONE
            holder.reportButton.visibility = View.GONE

            holder.itemView.setOnClickListener {
                Toast.makeText(
                    context,
                    "🚫 ${context.getString(R.string.blocked_video_message)}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        // Normal video setup
        holder.playerView.visibility = View.VISIBLE
        holder.videoTitle.text = videoTitle
        holder.likeButton.visibility = View.VISIBLE
        holder.shareButton.visibility = View.VISIBLE
        holder.reportButton.visibility = View.VISIBLE

        // Initialize player if needed
        if (holder.player == null) {
            holder.player = SimpleExoPlayer.Builder(context).build()
            holder.playerView.player = holder.player
            players.add(holder.player!!)
        }

        // Prepare media source
        val dataSourceFactory = DefaultDataSourceFactory(context, "kids-app")
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUri))

        holder.player?.setMediaSource(mediaSource)
        holder.player?.prepare()
        players.forEach { it.playWhenReady = false }

        if (holder.adapterPosition == currentPlayingPosition) {
            holder.player?.playWhenReady = true
        }

        // Set click listeners
        holder.likeButton.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val title = "Video ${adapterPosition + 1}"
                Toast.makeText(context, "Liked: $title", Toast.LENGTH_SHORT).show()
            }
        }

        holder.shareButton.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val url = videoUris[adapterPosition].toString()
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, url)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share video via"))
            }
        }

        holder.reportButton.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val title = "Video ${adapterPosition + 1}"
                Toast.makeText(context, "Reported: $title", Toast.LENGTH_SHORT).show()
            }
        }

        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            if (holder.isBlocked) {
                Toast.makeText(
                    context,
                    "🚫 ${context.getString(R.string.blocked_video_message)}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            currentPlayingPosition = adapterPosition
            notifyDataSetChanged()
            playListener?.onVideoPlayed("Video ${adapterPosition + 1}", videoUris[adapterPosition].toString())
        }
    }

    override fun getItemCount(): Int = videoUris.size

    fun stopAllVideos() {
        players.forEach { it.playWhenReady = false }
        currentPlayingPosition = -1
    }

    fun pauseAllVideos() {
        players.forEach { it.playWhenReady = false }
    }

    fun resumeAllVideos() {
        if (currentPlayingPosition != -1) {
            players.getOrNull(currentPlayingPosition)?.playWhenReady = true
        }
    }

    fun releasePlayers() {
        players.forEach { player ->
            player.playWhenReady = false
            player.release()
        }
        players.clear()
    }
}