package com.udacity.popularmovies

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.SharedElementCallback
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.udacity.popularmovies.data.Page
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TRANSITION_NAME = "TRANSITION_NAME"
        private val TAG = MainActivity::class.java.name.toString()
        private const val VIEW_POSITION = "VIEW_POSITION"
    }

    private lateinit var mRecyclerView: RecyclerView
    private var mMoviesAdapter = MoviesAdapter()
    private var mViewPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.rv_posters)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.adapter = mMoviesAdapter
        val span = resources.getInteger(R.integer.movie_grid_span)
        mRecyclerView.layoutManager = GridLayoutManager(this, span)

        getPopularMovies()

        if (savedInstanceState != null) {
            mViewPosition = savedInstanceState.getInt(VIEW_POSITION)
            setExitSharedElementCallback(object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: MutableList<String>?,
                    sharedElements: MutableMap<String, View>?
                ) {
                    super.onMapSharedElements(names, sharedElements)
                    if (sharedElements?.isEmpty()!!) {
                        val view = mRecyclerView.layoutManager?.findViewByPosition(mViewPosition)
                        if (view != null) {
                            sharedElements.put(names?.get(0)!!, view)
                        }
                        // TODO Scroll so that shared element is at the same vertical position.
                    }
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(VIEW_POSITION, mViewPosition)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_most_popular -> {
                item.isChecked = true
                mMoviesAdapter.setSortingMethod(MoviesAdapter.Sort.MOST_POPULAR)
                true
            }
            R.id.action_sort_by_highest_rated -> {
                item.isChecked = true
                mMoviesAdapter.setSortingMethod(MoviesAdapter.Sort.HIGHEST_RATED)
                true
            }
            else -> false
        }
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
