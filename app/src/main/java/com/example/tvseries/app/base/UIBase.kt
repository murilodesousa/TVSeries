package com.example.tvseries.app.base

import android.os.Bundle
import android.view.View
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import com.example.tvseries.MainActivity

open class UIBase: Fragment() {

    lateinit var mainActivity: MainActivity

    protected open fun bindView() {

    }

    protected open fun setListeners() {
        mainActivity.setVisibleFilter {
            setVisibleFilter()
        }
    }

    protected open fun obtainArguments() {

    }

    protected open fun setVisibleFilter(): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = (activity as MainActivity)
        obtainArguments()
        bindView()
        setListeners()
        configureFilter()
    }

    private fun configureFilter() {
        if (mainActivity.binding?.mainToolbar?.menu?.isNotEmpty() == true) {
            mainActivity.binding?.mainToolbar?.menu?.get(0)?.isVisible = setVisibleFilter()
        }
    }

}