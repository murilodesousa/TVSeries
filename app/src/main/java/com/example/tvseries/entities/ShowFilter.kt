package com.example.tvseries.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShowFilter(
    val show: Show?,
) : Parcelable