package com.example.tvseries.utils

import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.tvseries.R
import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(
    dateFormat: String = "yyyy-MM-dd",
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun ImageView.downloadImage(url: String?) {
    if (url != null) {
        url.let {
            val imgUri = it.toUri().buildUpon().scheme("https").build()
            Glide.with(this.context)
                .load(imgUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(this)
        }
    } else {
        this.setImageResource(R.drawable.ic_broken_image)
    }
}