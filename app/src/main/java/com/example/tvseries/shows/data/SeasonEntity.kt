package com.example.tvseries.shows.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SeasonEntity(
    val id: Long,
    val url: String?,
    val number: Long,
    val name: String?,
    val episodeOrder: Long?,
) : Parcelable