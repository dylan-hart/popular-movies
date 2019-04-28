package com.udacity.popularmovies.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Movie(@SerializedName("vote_count")           val voteCount: Int,
            @SerializedName("id")                   val id: Int,
            @SerializedName("video")                val video: Boolean,
            @SerializedName("vote_average")         val voteAverage: Float,
            @SerializedName("title")                val title: String,
            @SerializedName("popularity")           val popularity: Float,
            @SerializedName("poster_path")          val posterPath: String,
            @SerializedName("original_language")    val originalLanguage: String,
            @SerializedName("original_title")       val originalTitle: String,
            @SerializedName("genre_ids")            val genres: IntArray,
            @SerializedName("backdrop_path")        val backdropPath: String,
            @SerializedName("adult")                val adult: Boolean,
            @SerializedName("overview")             val overview: String,
            @SerializedName("release_date")         val releaseDate: String) : Parcelable {
    companion object {
        const val EXTRA_MOVIE = "EXTRA_MOVIE"
    }
}