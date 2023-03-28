package com.example.tvseries.episodes.presenter

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.tvseries.R
import com.example.tvseries.databinding.FragmentUiepisodeDetailBinding
import com.example.tvseries.episodes.data.EpisodeEntity
import com.example.tvseries.utils.downloadImage
import com.example.tvseries.app.base.UIBase

class UIEpisodeDetail: UIBase() {

    private var _binding: FragmentUiepisodeDetailBinding? = null
    private var _episode: EpisodeEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_uiepisode_detail, container, false)
        return _binding?.root
    }

    override fun obtainArguments() {
        super.obtainArguments()
        arguments?.let {
            _episode = UIEpisodeDetailArgs.fromBundle(it).episode
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @SuppressLint("SetTextI18n")
    override fun bindView() {
        _binding?.tvName?.text = _episode?.name
        _binding?.tvNumber?.text = "Temporada "+" "+_episode?.season.toString()+
                " - Episódio "+" "+_episode?.number.toString()
        _binding?.tvResume?.setText(Html.fromHtml(_episode?.summary))
        _binding?.tvResume?.isEnabled = false
        _binding?.ivEpisodeBanner?.downloadImage(_episode?.image?.get("original"))
    }

}