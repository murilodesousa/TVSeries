package com.example.tvseries.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
data class Show(

    val id: Long,
    val url: String?,
    val name: String,
    val type: String?,
    val language: String?,
    val genres: List<String>?,
    val status: String?,
    val runtime: Int?,
    val premiered: String?,
    val officialSite: String?,
    @SerialName("network")
    val airChannel: Channel?,

    val webChannel: Channel?,
    val image: Map<String, String>?,

    @SerialName("externals")
    val externalInfo: ExternalInfo?,

    val summary: String?,
    val rating: Map<String, String>?
) : Parcelable {
    @Parcelize
    data class Channel(
        val id: Long?,
        val name: String?,
        val country: Country?
    ) : Parcelable {
        @Parcelize
        data class Country(
            val name: String,
            val code: String,
            val timezone: String
        ) : Parcelable
    }

    @Parcelize
    data class ExternalInfo(
        val tvrage: String?,
        val thetvdb: Long?,
        val imdb: String?
    ) : Parcelable
}