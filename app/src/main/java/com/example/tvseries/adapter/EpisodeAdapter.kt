package com.example.tvseries.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tvseries.R
import com.example.tvseries.databinding.EpisodeItemBinding
import com.example.tvseries.entities.Episode
import com.example.tvseries.utils.downloadImage
import java.text.SimpleDateFormat
import com.example.tvseries.utils.toDate

class EpisodeAdapter(private val onItemSelected: (episode: Episode) -> Unit
): PagingDataAdapter<Episode, EpisodeAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Episode>() {

    override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
        return oldItem == newItem
    }

}) {

    private val _episodes = mutableListOf<Episode>()
    private lateinit var _lifecycleOwner: LifecycleOwner

    inner class ViewHolder(val binding: EpisodeItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(episode: Episode, position: Int) {
            binding.lifecycleOwner = _lifecycleOwner
            binding.tvName.text = episode.name
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val date = episode.airdate?.toDate()?.let { formatter.format(it) }
            binding.tvDate.text = "Lançamento: "+date+" "+episode.airtime
            binding.ivEpisodeBanner.downloadImage(episode.image?.get("medium"))
            binding.tvRuntime.text = "Duração: "+episode.runtime+" minutos"
            binding.clItem.setOnClickListener {
                onItemSelected(episode)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeAdapter.ViewHolder {
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

    override fun onBindViewHolder(holder: EpisodeAdapter.ViewHolder, position: Int) {
        if (_episodes.isNotEmpty() && position <= _episodes.lastIndex) {
            val episode = _episodes[position]
            holder.bind(episode, position)
        }
    }

    fun submitList(list: List<Episode>) {
        _episodes.clear()
        _episodes.addAll(list)
        notifyDataSetChanged()
    }

    fun defineLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        _lifecycleOwner = lifecycleOwner
    }

}