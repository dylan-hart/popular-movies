package com.udacity.popularmovies

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.udacity.popularmovies.data.Movie
import java.text.SimpleDateFormat
import java.util.*

class MovieDetailActivity : AppCompatActivity() {

    companion object {
        val TAG = MovieDetailActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val movie = intent.getParcelableExtra<Movie>(Movie.EXTRA_MOVIE)

        val moviePosterImageView = findViewById<ImageView>(R.id.iv_movie_poster)
        Picasso.get().load(MovieService.URL_POSTER + movie.posterPath).into(moviePosterImageView)

        val movieTitleTextView = findViewById<TextView>(R.id.tv_movie_title)
        movieTitleTextView.text = movie.title

        val movieYearTextView = findViewById<TextView>(R.id.tv_movie_year)
        movieYearTextView.text = getYear(movie.releaseDate)

        val movieMinutesTextView = findViewById<TextView>(R.id.tv_movie_minutes)
        movieMinutesTextView.text = "120min" // TODO Look up actual length.

        val movieRatingTextView = findViewById<TextView>(R.id.tv_movie_rating)
        movieRatingTextView.text = "${movie.voteAverage}/10 (${movie.voteCount})"

        val movieOverviewTextView = findViewById<TextView>(R.id.tv_movie_overview)
        movieOverviewTextView.text = movie.overview

        // TODO Process trailers.
    }

    private fun getYear(dateString: String): String {
        val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = formatDate.parse(dateString)
        val formatYear = SimpleDateFormat("yyyy", Locale.US)
        return formatYear.format(date)
    }
}