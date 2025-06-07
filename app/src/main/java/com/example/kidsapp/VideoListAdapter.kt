package com.example.kidsapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kidsapp.databinding.ItemSimpleVideoBinding

class VideoListAdapter(
    private val videos: List<VideoItem>,
    private val onClick: (VideoItem) -> Unit
) : RecyclerView.Adapter<VideoListAdapter.VH>() {

    inner class VH(private val binding: ItemSimpleVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(video: VideoItem) {
            binding.videoTitle.text = video.title
            binding.root.setOnClickListener { onClick(video) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSimpleVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount() = videos.size
}
