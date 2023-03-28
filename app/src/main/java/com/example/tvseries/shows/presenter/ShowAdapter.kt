package com.example.tvseries.shows.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tvseries.R
import com.example.tvseries.databinding.ShowItemBinding
import com.example.tvseries.shows.data.ShowEntity
import com.example.tvseries.utils.downloadImage

class ShowAdapter(private val onItemSelected: (showEntity: ShowEntity) -> Unit
): PagingDataAdapter<ShowEntity, ShowAdapter.ViewHolder>(object : DiffUtil.ItemCallback<ShowEntity>() {

    override fun areItemsTheSame(oldItem: ShowEntity, newItem: ShowEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShowEntity, newItem: ShowEntity): Boolean {
        return oldItem == newItem
    }

}) {

    private val _showEntities = mutableListOf<ShowEntity>()
    private lateinit var _lifecycleOwner: LifecycleOwner

    inner class ViewHolder(val binding: ShowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(showEntity: ShowEntity) {
            binding.lifecycleOwner = _lifecycleOwner
            binding.tvShowTitle.text = showEntity.name
            binding.ivShowBanner.downloadImage(showEntity.image?.get("medium"))
            binding.ivShowBanner.setOnClickListener {
                onItemSelected(showEntity)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.show_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return _showEntities.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (_showEntities.isNotEmpty() && position <= _showEntities.lastIndex) {
            val show = _showEntities[position]
            holder.bind(show)
        }
    }

    fun submitList(list: List<ShowEntity>) {
        _showEntities.addAll(list)
        notifyDataSetChanged()
    }

    fun defineLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        _lifecycleOwner = lifecycleOwner
    }

}