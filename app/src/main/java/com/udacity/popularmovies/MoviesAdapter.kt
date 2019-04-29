package com.udacity.popularmovies

import android.app.Activity
import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
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

    private var mMovies: Array<Movie> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.movie_grid_item, parent, false)
        val displayMetrics = DisplayMetrics()
        val activity: Activity = parent.context as Activity
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val rows = activity.resources.getInteger(R.integer.movie_grid_rows)
        return ViewHolder(view, height / rows)
    }

    override fun getItemCount(): Int {
        return mMovies.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val posterPath = mMovies[position].posterPath
        Picasso.get().load(MovieService.URL_POSTER + posterPath).into(holder.posterImageView)
        holder.posterImageView.transitionName = "movie_poster_$position"
        holder.posterImageView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(it.context as Activity,
                it,
                it.transitionName)
            val intent = Intent(it.context, MovieDetailActivity::class.java)
            intent.putExtra(Movie.EXTRA_MOVIE, mMovies[position])
            intent.putExtra(MainActivity.EXTRA_TRANSITION_NAME, it.transitionName)
            it.context.startActivity(intent, options.toBundle())
        }
    }

    fun setMovieData(movies: Array<Movie>) {
        this.mMovies = movies
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