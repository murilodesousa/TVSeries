package com.example.tvseries.shows.presenter

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tvseries.MainActivity
import com.example.tvseries.R
import com.example.tvseries.episodes.presenter.EpisodeAdapter
import com.example.tvseries.databinding.FragmentUishowDetailBinding
import com.example.tvseries.shows.data.ShowEntity
import com.example.tvseries.utils.downloadImage
import com.example.tvseries.app.base.UIBase


class UIShowDetail: UIBase() {

    private var _binding: FragmentUishowDetailBinding? = null
    private var _selectedShowEntity: ShowEntity? = null
    private var _episodeAdapter: EpisodeAdapter? = null

    private val viewModel: VMShow by lazy {
        ViewModelProvider(this)[VMShow::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_uishow_detail, container,false)
        return _binding?.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun obtainArguments() {
        super.obtainArguments()
        arguments?.let {
            _selectedShowEntity = UIShowDetailArgs.fromBundle(it).show
        }
    }

    override fun setListeners() {
        viewModel.seasons.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                it.let {
                    val hintText = _binding?.root?.resources?.getString(R.string.show_season)
                    val seasonsName = mutableListOf(hintText)
                    it.forEach {
                        seasonsName.add(_binding?.root?.resources?.getString(
                            R.string.show_season_number,
                            it.number.toString()) ?: ""
                        )
                    }
                    val spinnerAdapter = createSpinnerAdapter(seasonsName)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    _binding?.spSeason?.adapter = spinnerAdapter
                    if (viewModel.selectedPositionSeason != null) {
                        _binding?.spSeason?.setSelection(viewModel.selectedPositionSeason ?: 0)
                    }
                }
            }
        }
        viewModel.requestStatus.observe(viewLifecycleOwner) {
            when (it) {
                VMShow.RequestStatus.Idle -> setViewLoading(false, false)
                VMShow.RequestStatus.Fetching -> setViewLoading(true, false)
                VMShow.RequestStatus.Error -> setViewLoading(false, true)
                VMShow.RequestStatus.Done -> setViewLoading(false, false)
            }
        }
        viewModel.episodesBySeason.observe(viewLifecycleOwner) {
            it?.let {
                _episodeAdapter?.submitList(it)
            }
        }
        _binding?.spSeason?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    val selectedSeason = viewModel.seasons.value?.get(position - 1)
                    selectedSeason?.number?.toInt()?.let {
                        viewModel.selectSeason(selectedSeason)
                        viewModel.selectPositionSeason(position)
                        viewModel.selectEpisodesBySeason(it)
                    }
                }
            }
        }
        _binding?.ivRefresh?.setOnClickListener {
            selectShow()
        }
    }

    private fun  selectShow() {
        _selectedShowEntity.let {
            if (it != null) {
                viewModel.selectShow(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindView() {
        _episodeAdapter = EpisodeAdapter {
            findNavController().navigate (
                UIShowDetailDirections.actionUIShowDetailToUIEpisodeDetail(it)
            )
        }
        _binding?.rvEpisodes?.layoutManager = LinearLayoutManager(requireContext())
        _episodeAdapter?.defineLifecycleOwner(this)
        _binding?.rvEpisodes?.adapter = _episodeAdapter
        selectShow()
        _binding?.tvShowTitle?.text = _selectedShowEntity?.name
        _binding?.ivShowBanner?.downloadImage(_selectedShowEntity?.image?.get("medium"))
        if (_selectedShowEntity?.genres?.isNotEmpty() == true) {
            val last = _selectedShowEntity?.genres?.last()
            _selectedShowEntity?.genres?.forEach {
                val value = _binding?.tvGenereText?.text
                _binding?.tvGenereText?.text = value.toString() + it + if (it != last) {
                    " | "
                } else {
                    ""
                }
            }
        } else {
            _binding?.tvGenere?.isVisible = false
        }
        _binding?.tvSumary?.setText(Html.fromHtml(_selectedShowEntity?.summary))
    }

    private fun setViewLoading(loading: Boolean, error: Boolean) {
        if (error) {
            _binding?.clFail?.visibility = View.VISIBLE
            _binding?.clForm?.visibility = View.GONE
            _binding?.ivLoading?.visibility = View.GONE
        } else {
            _binding?.clFail?.visibility = View.GONE
            _binding?.clForm?.visibility = if (loading) {
                View.GONE
            } else {
                View.VISIBLE
            }
            _binding?.ivLoading?.visibility = if (loading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun createSpinnerAdapter(seasons: MutableList<String?>) : ArrayAdapter<String> {
        return object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            seasons
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val spinnerView = super.getView(position, convertView, parent)

                if (position == 0)
                    (spinnerView as TextView).setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.solidGrayColor,
                            null
                        )
                    )

                return spinnerView
            }

            override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
                val spinnerView = super.getDropDownView(position, recycledView, parent)

                if (position == 0) {
                    (spinnerView as TextView).setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.solidGrayColor,
                            null
                        )
                    )
                    spinnerView.setBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.lineGrayColor,
                            null
                        )
                    )
                    spinnerView.setOnClickListener(null)
                }

                val currentSeason = viewModel.selectedSeasonEntity?.name
                if (currentSeason == seasons[position]) {
                    spinnerView.setBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.selectedItemBackground,
                            null
                        )
                    )
                }

                return spinnerView
            }
        }
    }

}