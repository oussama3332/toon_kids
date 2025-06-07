package com.example.kidsapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kidsapp.databinding.ItemVideoReelBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class VideoPagerAdapter(
    private val context: Context,
    private val videoUris: List<Uri>
) : RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder>() {

    private var currentPlayer: ExoPlayer? = null
    private val players = mutableMapOf<Int, ExoPlayer>()

    inner class VideoViewHolder(val binding: ItemVideoReelBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoReelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUris[position])
            setMediaItem(mediaItem)
            repeatMode = ExoPlayer.REPEAT_MODE_ONE // تكرار الفيديو تلقائياً
            prepare()

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        seekTo(0)
                        play()
                    }
                }
            })
        }

        players[position] = player
        holder.binding.playerView.player = player
        holder.binding.playerView.useController = false
    }

    override fun getItemCount(): Int = videoUris.size

    fun playVideo(position: Int) {
        currentPlayer?.pause()
        currentPlayer = players[position]
        currentPlayer?.play()
    }

    fun pauseCurrentVideo() {
        currentPlayer?.pause()
    }

    fun releaseAllPlayers() {
        players.values.forEach { it.release() }
        players.clear()
        currentPlayer = null
    }
}