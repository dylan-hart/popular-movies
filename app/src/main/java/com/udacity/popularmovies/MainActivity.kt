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
import com.udacity.popularmovies.data.Movie
import com.udacity.popularmovies.data.Page
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TRANSITION_NAME = "TRANSITION_NAME"
        private val TAG = MainActivity::class.java.name.toString()
        private const val VIEW_POSITION = "VIEW_POSITION"
    }

    enum class MovieType {
        UNINITIALIZED,
        MOST_POPULAR,
        TOP_RATED
    }

    private lateinit var mRecyclerView: RecyclerView
    private var mMoviesAdapter = MoviesAdapter()
    private var mViewPosition = 0
    private var mPopularMovies: Array<Movie>? = null
    private var mTopRatedMovies: Array<Movie>? = null
    private var mMovieType = MovieType.UNINITIALIZED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.rv_posters)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.adapter = mMoviesAdapter
        val span = resources.getInteger(R.integer.movie_grid_span)
        mRecyclerView.layoutManager = GridLayoutManager(this, span)

        showPopularMovies()

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
                showPopularMovies()
                true
            }
            R.id.action_sort_by_highest_rated -> {
                item.isChecked = true
                showTopRatedMovies()
                true
            }
            else -> false
        }
    }

    private fun showPopularMovies() {
        if (mMovieType != MovieType.MOST_POPULAR) {
            mMovieType = MovieType.MOST_POPULAR
            if (mPopularMovies != null) {
                mMoviesAdapter.setMovieData(mPopularMovies!!)
            } else {
                val movieService = MovieService.create()
                val call = movieService.requestPopularMovies(BuildConfig.API_KEY_TMDB)
                call.enqueue(object : Callback<Page> {
                    override fun onResponse(call: Call<Page>, response: Response<Page>?) {
                        if (response != null) {
                            mPopularMovies = response.body()?.movies
                            mMoviesAdapter.setMovieData(mPopularMovies!!)
                        }
                    }

                    override fun onFailure(call: Call<Page>, t: Throwable) {
                        Log.e(TAG, t.toString())
                    }
                })
            }
        }
    }

    private fun showTopRatedMovies() {
        if (mMovieType != MovieType.TOP_RATED) {
            mMovieType = MovieType.TOP_RATED
            if (mTopRatedMovies != null) {
                mMoviesAdapter.setMovieData(mTopRatedMovies!!)
            } else {
                val movieService = MovieService.create()
                val call = movieService.requestTopRatedMovies(BuildConfig.API_KEY_TMDB)
                call.enqueue(object : Callback<Page> {
                    override fun onResponse(call: Call<Page>, response: Response<Page>?) {
                        if (response != null) {
                            mTopRatedMovies = response.body()?.movies
                            mMoviesAdapter.setMovieData(mTopRatedMovies!!)
                        }
                    }

                    override fun onFailure(call: Call<Page>, t: Throwable) {
                        Log.e(TAG, t.toString())
                    }
                })
            }
        }
    }
}
