package com.example.tvseries.episodes.data

import android.os.Parcelable
import com.example.tvseries.shows.data.ShowEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EpisodeEntity(
    val showEntity: ShowEntity?,
    val id: Long?,
    val url: String?,
    val name: String?,
    val season: Int?,
    val number: Int?,
    val airdate: String?,
    val airtime: String?,
    val runtime: Int?,
    val image: Map<String, String>?,
    val summary: String?,
) : Parcelable
