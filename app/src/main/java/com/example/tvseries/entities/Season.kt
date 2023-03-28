package com.example.tvseries.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Season(
    val id: Long,
    val url: String?,
    val number: Long,
    val name: String?,
    val episodeOrder: Long?,
) : Parcelable