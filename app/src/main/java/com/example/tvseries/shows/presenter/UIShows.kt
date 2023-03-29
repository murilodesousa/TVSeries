package com.example.tvseries.shows.presenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvseries.R
import com.example.tvseries.databinding.FragmentUishowBinding
import com.example.tvseries.app.base.UIBase

class UIShows: UIBase() {

    private var _binding: FragmentUishowBinding? = null
    private var _adapter: ShowAdapter? = null

    private val viewModel: VMShow by lazy {
        ViewModelProvider(this)[VMShow::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_uishow,
            container,
            false
        )
        return _binding?.root
    }

    override fun onDestroyView() {
        _adapter = null
        _binding = null
        super.onDestroyView()
    }

    override fun bindView() {
        super.bindView()
        _adapter = ShowAdapter() {
            findNavController().navigate(
                UIShowsDirections.actionUIShowsToUIShowDetail(it)
            )
        }
        _binding?.rvItems?.layoutManager = GridLayoutManager(context, 3)
        _adapter?.defineLifecycleOwner(this)
        _binding?.rvItems?.adapter = _adapter
        _binding?.rvItems?.addOnScrollListener(recyclerViewScrollListener)
        viewModel.clear()
        viewModel.getShows()
    }

    override fun setListeners() {
        super.setListeners()
        viewModel.shows.observe(viewLifecycleOwner) {
            it?.let {
                _adapter?.submitList(it)
            }
        }

        viewModel.requestStatus.observe(viewLifecycleOwner) {
            when (it) {
                VMShow.RequestStatus.Idle -> setViewLoading(false, false)
                VMShow.RequestStatus.Fetching -> setViewLoading(viewModel.isFirstPage(), false)
                VMShow.RequestStatus.Error -> setViewLoading(false, true)
                VMShow.RequestStatus.Done -> setViewLoading(false, false)
            }
        }

        _binding?.ivRefresh?.setOnClickListener {
            viewModel.clear()
            viewModel.getShows()
        }

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

    private val recyclerViewScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            (recyclerView.layoutManager as? GridLayoutManager)?.let { gridLayout ->
                if (!viewModel.fetchingApi()) {
                    val visibleItemCount = gridLayout.childCount
                    val firstVisibleItem = gridLayout.findFirstCompletelyVisibleItemPosition()
                    val totalItemCount = _adapter?.itemCount ?: 0
                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                        viewModel.getShows()
                    }
                }
            }
        }
    }

}