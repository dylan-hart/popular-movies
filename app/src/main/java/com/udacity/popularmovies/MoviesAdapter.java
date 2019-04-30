package com.udacity.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.data.Movie;

import java.util.Locale;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final static String TAG = MoviesAdapter.class.getName();

    private Movie[] mMovies = {};

    @NonNull
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_grid_item, parent, false);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Activity activity = (Activity) parent.getContext();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int rows = activity.getResources().getInteger(R.integer.movie_grid_rows);
        return new ViewHolder(view, height / rows);
    }

    @Override
    public int getItemCount() {
        return mMovies.length;
    }

    @Override
    public void onBindViewHolder(@NonNull final MoviesAdapter.ViewHolder holder, int position) {
        String posterPath = mMovies[position].getPosterPath();
        Picasso.get()
                .load(RetroFitUtils.getPosterUrl(holder.posterImageView.getContext(), posterPath))
                .placeholder(R.drawable.movie_poster_placeholder)
                .into(holder.posterImageView);
        holder.posterImageView.setTransitionName(String.format(Locale.US, "movie_poster_%d", position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView poster = v.findViewById(R.id.iv_poster);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)v.getContext(),
                        poster,
                        poster.getTransitionName());
                Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
                intent.putExtra(Movie.EXTRA_MOVIE, mMovies[holder.getAdapterPosition()]);
                intent.putExtra(MainActivity.EXTRA_TRANSITION_NAME, poster.getTransitionName());
                v.getContext().startActivity(intent, options.toBundle());
            }
        });
    }

    public void setMovieData(Movie[] movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
            ImageView posterImageView;
        ViewHolder(@NonNull View itemView, int height) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.iv_poster);
            ViewGroup.LayoutParams layoutParams = posterImageView.getLayoutParams();
            layoutParams.height = height;
            posterImageView.setLayoutParams(layoutParams);
        }
    }
}
