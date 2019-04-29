package com.udacity.popularmovies

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.udacity.popularmovies.data.Movie
import com.udacity.popularmovies.data.MovieDetails
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*

class MovieDetailActivity : AppCompatActivity() {

    companion object {
        val TAG = MovieDetailActivity::class.java.name
    }

    lateinit var movieMinutesTextView: TextView
    private var mOriginalOrientation: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val movie = intent.getParcelableExtra<Movie>(Movie.EXTRA_MOVIE)

        val moviePosterImageView = findViewById<ImageView>(R.id.iv_movie_poster)
        moviePosterImageView.transitionName = intent.getStringExtra(MainActivity.EXTRA_TRANSITION_NAME)
        Picasso.get()
            .load(MovieService.URL_POSTER + movie.posterPath)
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(moviePosterImageView)

        val movieTitleTextView = findViewById<TextView>(R.id.tv_movie_title)
        movieTitleTextView.text = movie.title

        val movieYearTextView = findViewById<TextView>(R.id.tv_movie_year)
        movieYearTextView.text = getYear(movie.releaseDate)

        movieMinutesTextView = findViewById<TextView>(R.id.tv_movie_minutes)

        val movieRatingTextView = findViewById<TextView>(R.id.tv_movie_rating)
        movieRatingTextView.text = "${movie.voteAverage}/10 (${movie.voteCount})"

        val movieOverviewTextView = findViewById<TextView>(R.id.tv_movie_overview)
        movieOverviewTextView.text = movie.overview

        mOriginalOrientation = resources.configuration.orientation

        // TODO Process trailers.

        getMovieDetails(movie.id)
    }

    private fun getYear(dateString: String): String {
        val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = formatDate.parse(dateString)
        val formatYear = SimpleDateFormat("yyyy", Locale.US)
        return formatYear.format(date)
    }

    private fun getMovieDetails(movieId: Int) {
        val movieService = MovieService.create()
        val call = movieService.requestMovieDetails(movieId, BuildConfig.API_KEY_TMDB)
        call.enqueue(object : Callback<MovieDetails> {
            override fun onResponse(call: Call<MovieDetails>, response: retrofit2.Response<MovieDetails>?) {
                if (response != null) {
                    val runtime = response.body()?.runtime
                    if (runtime != null) {
                        movieMinutesTextView.text = "${runtime}min"
                        movieMinutesTextView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                Log.e(TAG, t.toString())
            }
        })
    }
}