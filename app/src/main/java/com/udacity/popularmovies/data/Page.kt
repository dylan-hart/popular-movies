package com.udacity.popularmovies.data

import com.google.gson.annotations.SerializedName

class Page {
    @SerializedName("page")
    var pageNumber: Int = 0

    @SerializedName("total_results")
    var totalResults: Int = 0

    @SerializedName("total_pages")
    var totalPages: Int = 0

    @SerializedName("results")
    var movies: Array<Movie>? = null
}