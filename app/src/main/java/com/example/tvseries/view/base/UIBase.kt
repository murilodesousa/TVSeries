package com.example.tvseries.view.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

open class UIBase: Fragment() {

    protected open fun bindView() {

    }

    protected open fun setListeners() {

    }

    protected open fun obtainArguments() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        obtainArguments()
        bindView()
        setListeners()
    }

}