package com.udacity.popularmovies

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.udacity.popularmovies.data.Movie

class MoviesAdapter : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    private val movies: Array<Movie>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.movie_grid_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        var count = 0
        if (movies != null) count = movies.size
        return count
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (movies != null) {
            val posterPath = movies[position].posterPath
            // TODO Send to Picasso.
            // TODO Assign to imageview.
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            val posterImageView: ImageView = itemView.findViewById(R.id.iv_poster)
        }
    }
}