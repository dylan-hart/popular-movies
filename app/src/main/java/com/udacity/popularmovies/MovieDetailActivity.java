package com.udacity.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.data.Movie;
import com.udacity.popularmovies.data.MovieDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getName();

    private TextView mMovieMinutesTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie movie = getIntent().getParcelableExtra(Movie.EXTRA_MOVIE);

        ImageView moviePosterImageView = findViewById(R.id.iv_movie_poster);
        moviePosterImageView.setTransitionName(getIntent().getStringExtra(MainActivity.EXTRA_TRANSITION_NAME));
        Picasso.get()
                .load(RetroFitUtils.getPosterUrl(this, movie.getPosterPath()))
                .placeholder(R.drawable.movie_poster_placeholder)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(moviePosterImageView);

        TextView movieTitleTextView = findViewById(R.id.tv_movie_title);
        movieTitleTextView.setText(movie.getTitle());

        TextView movieYearTextView = findViewById(R.id.tv_movie_year);
        movieYearTextView.setText(getYear(movie.getReleaseDate()));

        mMovieMinutesTextView = findViewById(R.id.tv_movie_minutes);

        TextView movieRatingTextView = findViewById(R.id.tv_movie_rating);
        movieRatingTextView.setText(String.format(Locale.US, "%.1f/10 (%d)",
                movie.getVoteAverage(),
                movie.getVoteCount()));

        TextView movieOverviewTextView = findViewById(R.id.tv_movie_overview);
        movieOverviewTextView.setText(movie.getOverview());

        // TODO (Part II) Process trailers.

        getMovieDetails(movie.getId());
    }

    private String getYear(String dateString) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = formatDate.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy", Locale.US);
        return formatYear.format(date);
    }

    private void getMovieDetails(int movieId) {
        MovieService movieService = RetroFitUtils.createMovieService();
        Call<MovieDetails> call = movieService.requestMovieDetails(movieId, BuildConfig.API_KEY_TMDB);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                int runtime = response.body().getRuntime();
                mMovieMinutesTextView.setText(String.format(Locale.US, "%dmin", runtime));
                mMovieMinutesTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }
}