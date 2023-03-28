package com.example.tvseries.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tvseries.R
import com.example.tvseries.adapter.EpisodeAdapter
import com.example.tvseries.databinding.FragmentUishowDetailBinding
import com.example.tvseries.entities.Show
import com.example.tvseries.utils.downloadImage
import com.example.tvseries.view.base.UIBase
import com.example.tvseries.viewmodel.VMShow


class UIShowDetail: UIBase() {

    private var _binding: FragmentUishowDetailBinding? = null
    private var _selectedShow: Show? = null
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
            _selectedShow = UIShowDetailArgs.fromBundle(it).show
        }
    }

    override fun setListeners() {
        viewModel.seasons.observe(viewLifecycleOwner) {
            it?.let {
                val hintText = "Temporada...  "
                val seasonsName = mutableListOf(hintText)
                it.forEach {
                    if (it.episodeOrder != null) {
                        seasonsName.add("Temporada "+it.number.toString())
                    }
                }
                val spinnerAdapter = createSpinnerAdapter(seasonsName)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                _binding?.spSeason?.adapter = spinnerAdapter
                if (viewModel.selectedPositionSeason != null) {
                    _binding?.spSeason?.setSelection(viewModel.selectedPositionSeason ?: 0)
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
        _selectedShow.let {
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
        _binding?.tvShowTitle?.text = _selectedShow?.name
        _binding?.ivShowBanner?.downloadImage(_selectedShow?.image?.get("medium"))
        val last = _selectedShow?.genres?.last()
        _selectedShow?.genres?.forEach {
            val value = _binding?.tvGenereText?.text
            _binding?.tvGenereText?.text = value.toString() + it + if (it != last) {" | "} else {""}
        }
        _binding?.tvSumary?.setText(Html.fromHtml(_selectedShow?.summary))
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

    private fun createSpinnerAdapter(seasons: List<String>) : ArrayAdapter<String> {
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

                val currentSeason = viewModel.selectedSeason?.name
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