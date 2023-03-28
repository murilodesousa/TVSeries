package com.example.tvseries.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tvseries.R
import com.example.tvseries.databinding.ShowItemBinding
import com.example.tvseries.entities.Show
import com.example.tvseries.utils.downloadImage
import com.example.tvseries.viewmodel.VMShow

class ShowAdapter(private val onItemSelected: (show: Show) -> Unit
): PagingDataAdapter<Show, ShowAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Show>() {

    override fun areItemsTheSame(oldItem: Show, newItem: Show): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Show, newItem: Show): Boolean {
        return oldItem == newItem
    }

}) {

    private val _shows = mutableListOf<Show>()
    private lateinit var _lifecycleOwner: LifecycleOwner

    inner class ViewHolder(val binding: ShowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(show: Show) {
            binding.lifecycleOwner = _lifecycleOwner
            binding.tvShowTitle.text = show.name
            binding.ivShowBanner.downloadImage(show.image?.get("medium"))
            binding.ivShowBanner.setOnClickListener {
                onItemSelected(show)
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
        return _shows.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (_shows.isNotEmpty() && position <= _shows.lastIndex) {
            val show = _shows[position]
            holder.bind(show)
        }
    }

    fun submitList(list: List<Show>) {
        _shows.addAll(list)
        notifyDataSetChanged()
    }

    fun defineLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        _lifecycleOwner = lifecycleOwner
    }

}