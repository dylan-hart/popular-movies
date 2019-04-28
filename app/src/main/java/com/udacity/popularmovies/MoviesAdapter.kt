package com.udacity.popularmovies

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.udacity.popularmovies.data.Movie

class MoviesAdapter : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    companion object {
        private val TAG = MoviesAdapter::class.java.name
    }

    private var movies: Array<Movie> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.movie_grid_item, parent, false)
        val displayMetrics = DisplayMetrics()
        // TODO Create utility function to determine best height (consider landscape)
        val activity: Activity = parent.context as Activity
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return ViewHolder(view, displayMetrics.heightPixels / 2)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val posterPath = movies[position].posterPath
        Picasso.get().load(MovieService.URL_POSTER + posterPath).into(holder.posterImageView)
        holder.posterImageView.setOnClickListener {
            val intent = Intent(holder.posterImageView.context, MovieDetailActivity::class.java)
            intent.putExtra(Movie.EXTRA_MOVIE, movies[position])
            holder.posterImageView.context.startActivity(intent)
        }
    }

    fun setMovieData(movies: Array<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View, height: Int) : RecyclerView.ViewHolder(itemView) {
        val posterImageView: ImageView = itemView.findViewById(R.id.iv_poster)
        init {
            val layoutParams = posterImageView.layoutParams
            layoutParams.height = height
            posterImageView.layoutParams = layoutParams
        }
    }
}