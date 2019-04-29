package com.udacity.popularmovies.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class MovieDetails(@SerializedName("budget")    val budget: Int,
                   @SerializedName("revenue")   val revenue: Int,
                   @SerializedName("runtime")   val runtime: Int) : Parcelable