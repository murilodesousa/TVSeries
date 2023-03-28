package com.example.tvseries.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Episode(
    val show: Show?,
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
