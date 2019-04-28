package com.udacity.popularmovies

import com.udacity.popularmovies.data.Page
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("popular")
    fun requestPopularMovies(@Query("api_key") apiKey: String) : Call<Page>

    companion object Factory {
        private const val URL_POPULAR_MOVIES = "http://api.themoviedb.org/3/movie/"
        private const val URL_POSTER = "http://image.tmdb.org/t/p/w185"
        fun create(): MovieService {
            val retrofit = Retrofit.Builder()
                .baseUrl(URL_POPULAR_MOVIES)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(MovieService::class.java)
        }
    }
}