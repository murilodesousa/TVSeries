package com.example.tvseries.episodes.presenter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.tvseries.R
import com.example.tvseries.databinding.EpisodeItemBinding
import com.example.tvseries.episodes.data.EpisodeEntity
import com.example.tvseries.utils.downloadImage
import java.text.SimpleDateFormat
import com.example.tvseries.utils.toDate

class EpisodeAdapter(private val onItemSelected: (episode: EpisodeEntity) -> Unit
): RecyclerView.Adapter<EpisodeAdapter.ViewHolder>()  {

    private val _episodes = mutableListOf<EpisodeEntity>()
    private lateinit var _lifecycleOwner: LifecycleOwner

    inner class ViewHolder(val binding: EpisodeItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(episode: EpisodeEntity, position: Int) {
            binding.lifecycleOwner = _lifecycleOwner
            binding.tvName.text = episode.name
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val date = episode.airdate?.toDate()?.let { formatter.format(it) }
            binding.tvDate.text = String.format(
                binding.root.resources.getString(R.string.episode_release_date),
                date,
                episode.airtime
            )
            binding.ivEpisodeBanner.downloadImage(episode.image?.get("medium"))
            binding.tvRuntime.text = binding.root.resources.getString(R.string.episode_duration,
                episode.runtime.toString())
            binding.clItem.setOnClickListener {
                onItemSelected(episode)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.episode_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return _episodes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (_episodes.isNotEmpty() && position <= _episodes.lastIndex) {
            val episode = _episodes[position]
            holder.bind(episode, position)
        }
    }

    fun submitList(list: List<EpisodeEntity>) {
        _episodes.clear()
        _episodes.addAll(list)
        notifyDataSetChanged()
    }

    fun defineLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        _lifecycleOwner = lifecycleOwner
    }

}