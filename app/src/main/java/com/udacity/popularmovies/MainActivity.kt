package com.udacity.popularmovies

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.udacity.popularmovies.data.Page
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.name.toString()
        // TODO Create utility function to auto-calc span
        private const val SPAN = 2
    }

    private lateinit var mRecyclerView: RecyclerView
    private var mMoviesAdapter = MoviesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.rv_posters)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.adapter = mMoviesAdapter
        mRecyclerView.layoutManager = GridLayoutManager(this, SPAN)

        getPopularMovies()
    }

    private fun getPopularMovies() {
        val movieService = MovieService.create()
        val call = movieService.requestPopularMovies(BuildConfig.API_KEY_TMDB)
        call.enqueue(object : Callback<Page> {
            override fun onResponse(call: Call<Page>, response: retrofit2.Response<Page>?) {
                if (response != null) {
                    val movies = response.body()?.movies!!
                    mMoviesAdapter.setMovieData(movies)
                }
            }

            override fun onFailure(call: Call<Page>, t: Throwable) {
                Log.e(TAG, t.toString())
            }
        })
    }
}
