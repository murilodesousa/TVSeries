package com.example.tvseries.shows.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShowFilterEntity(
    val showEntity: ShowEntity?,
) : Parcelable