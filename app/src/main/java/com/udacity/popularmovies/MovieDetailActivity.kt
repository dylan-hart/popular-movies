package com.udacity.popularmovies

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.udacity.popularmovies.data.Movie
import kotlinx.android.synthetic.main.activity_detail.*

class MovieDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO Inflate layout.
        setContentView(R.layout.activity_detail)

        val movie = intent.getParcelableExtra<Movie>(Movie.EXTRA_MOVIE)

        val moviePosterImageView = findViewById<ImageView>(R.id.iv_movie_poster)
        Picasso.get().load(MovieService.URL_POSTER + movie.posterPath).into(moviePosterImageView)

        val movieTitleTextView = findViewById<TextView>(R.id.tv_movie_title)
        movieTitleTextView.text = movie.title

        val movieYearTextView = findViewById<TextView>(R.id.tv_movie_year)
        movieYearTextView.text = movie.releaseDate

        val movieMinutesTextView = findViewById<TextView>(R.id.tv_movie_minutes)
        movieMinutesTextView.text = "120min" // TODO Look up actual length.

        val movieRatingTextView = findViewById<TextView>(R.id.tv_movie_rating)
        movieRatingTextView.text = "${movie.voteAverage}/10 (${movie.voteCount})"

        val movieOverviewTextView = findViewById<TextView>(R.id.tv_movie_overview)
        tv_movie_overview.text = movie.overview

        // TODO Process trailers.
    }
}