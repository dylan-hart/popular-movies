package com.udacity.popularmovies;

import android.app.SharedElementCallback;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.data.*;
import com.udacity.popularmovies.database.AppDatabase;
import com.udacity.popularmovies.database.MovieDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getName();

    private Movie mMovie;
    private TextView mMovieMinutesTextView;
    private Button mMovieFavoriteButton;
    private LinearLayout mTrailersLinearLayout;
    private LinearLayout mReviewsLinearLayout;

    private LinearLayout.LayoutParams LinearLayoutLayoutParams;
    private LinearLayout.LayoutParams mTrailerImageViewLayoutParams;
    private LinearLayout.LayoutParams mTrailerTextViewLayoutParams;
    private LinearLayout.LayoutParams mHorizontalRuleLayoutParams;
    private LinearLayout.LayoutParams mReviewLabelTextViewLayoutParams;
    private LinearLayout.LayoutParams mReviewAuthorTextViewLayoutParams;

    private AppDatabase mAppDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /* Get a reference to the database and get the movie
         * associated with this MovieDetailActivity.
         */

        mAppDatabase = AppDatabase.getInstance(getApplicationContext());
        mMovie = getIntent().getParcelableExtra(Movie.EXTRA_MOVIE);
        LiveData<Movie> movie = mAppDatabase.movieDao().load(mMovie.getId());
        movie.observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                Log.d(TAG, "Receiving database update from LiveData.");
                if (movie != null) {
                    mMovie = movie;
                    if (mMovie.getIsFavorite()) {
                        mMovieFavoriteButton.setBackground(getDrawable(R.drawable.ic_star_black_24dp));
                    }
                }
            }
        });

        /* Convert dp to pixels.
         */

        int dp48 = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                48f,
                getResources().getDisplayMetrics()
        );

        int dp24 = dp48 / 2;
        int dp12 = dp24 / 2;
        int dp2 = dp12 / 6;

        /* Set up layout params for programmatically
         * adding trailers and reviews later.
         */

        LinearLayoutLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayoutLayoutParams.setMargins(dp24, dp12, dp24, 0);

        mTrailerImageViewLayoutParams = new LinearLayout.LayoutParams(dp48, dp48);
        mTrailerImageViewLayoutParams.setMargins(0,0, dp24,0);

        mTrailerTextViewLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1);

        mHorizontalRuleLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp2
        );
        mHorizontalRuleLayoutParams.setMargins(dp24, dp12, dp24, 0);

        mReviewLabelTextViewLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mReviewAuthorTextViewLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );

        // Load the movie's poster.

        ImageView moviePosterImageView = findViewById(R.id.iv_movie_poster);
        moviePosterImageView.setTransitionName("poster");
        Picasso.get()
                .load(RetroFitUtils.getPosterUrl(this, mMovie.getPosterPath()))
                .placeholder(R.drawable.movie_poster_placeholder)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(moviePosterImageView);

        // Display the movie's title.

        TextView movieTitleTextView = findViewById(R.id.tv_movie_title);
        movieTitleTextView.setText(mMovie.getTitle());

        // Display the movie's release date.

        TextView movieYearTextView = findViewById(R.id.tv_movie_year);
        movieYearTextView.setText(getYear(mMovie.getReleaseDate()));

        /* Get a reference to the layout's TextView for movie duration.
         * (A network request needs to be sent to retrieve the movie duration,
         * so it will be filled in later).
         */

        mMovieMinutesTextView = findViewById(R.id.tv_movie_minutes);

        // Display the movie's average rating and vote count.

        TextView movieRatingTextView = findViewById(R.id.tv_movie_rating);
        movieRatingTextView.setText(String.format(Locale.US, "%.1f/10 (%d)",
                mMovie.getVoteAverage(),
                mMovie.getVoteCount()));

        /* Get a reference to the favorite button, update its state,
         * and listen for clicks.
         */

        mMovieFavoriteButton = findViewById(R.id.btn_mark_favorite);
        if (mMovie.getIsFavorite()) {
            mMovieFavoriteButton.setBackground(getDrawable(R.drawable.ic_star_black_24dp));
        }
        findViewById(R.id.btn_mark_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });

        // Display the movie's overview.

        TextView movieOverviewTextView = findViewById(R.id.tv_movie_overview);
        movieOverviewTextView.setText(mMovie.getOverview());

        /* Get references to the layout's LinearLayouts for trailers
         * and reviews. (Network requests need to be sent to retrieve
         * the trailers and reviews, so they will be added later).
         */

        mTrailersLinearLayout = findViewById(R.id.ll_trailers);
        mReviewsLinearLayout = findViewById(R.id.ll_reviews);

        getMovieDetails(mMovie.getId());
        getTrailers(mMovie.getId());
        getReviews(mMovie.getId());
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
            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {
                MovieDetails details = response.body();
                if (details != null) {
                    mMovieMinutesTextView.setText(String.format(Locale.US, "%dmin", details.getRuntime()));
                    mMovieMinutesTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void getTrailers(int movieId) {
        MovieService movieService = RetroFitUtils.createMovieService();
        Call<MovieTrailers> call = movieService.requestMovieTrailers(movieId, BuildConfig.API_KEY_TMDB);
        call.enqueue(new Callback<MovieTrailers>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<MovieTrailers> call, Response<MovieTrailers> response) {
                MovieTrailers movieTrailers = response.body();
                if (movieTrailers != null) {
                    Trailer[] trailers = movieTrailers.getTrailers();
                    if (movieTrailers.getTrailers() != null) {
                        if (trailers.length > 0) mTrailersLinearLayout.setVisibility(View.VISIBLE);
                        int count = 1;
                        for (Trailer t : trailers) {
                            if (t.getType().equals("Trailer") && t.getSite().equals("YouTube")) {
                                LinearLayout linearLayout = new LinearLayout(getBaseContext());
                                linearLayout.setLayoutDirection(LinearLayout.HORIZONTAL);
                                linearLayout.setLayoutParams(LinearLayoutLayoutParams);
                                final String key = t.getKey();
                                linearLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("vnd.youtube:" + key));
                                        if (intent.resolveActivity(getPackageManager()) == null) {
                                            intent = new Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("https://www.youtube.com/watch?v=" + key));
                                        }
                                        startActivity(intent);
                                    }
                                });
                                ImageView imageView = new ImageView(getBaseContext());
                                imageView.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                                imageView.setLayoutParams(mTrailerImageViewLayoutParams);
                                linearLayout.addView(imageView);
                                TextView textView = new TextView(getBaseContext());
                                textView.setLayoutParams(mTrailerTextViewLayoutParams);
                                textView.setText(getString(R.string.movie_detail_trailer_label, count));
                                textView.setGravity(Gravity.CENTER_VERTICAL);
                                linearLayout.addView(textView);
                                mTrailersLinearLayout.addView(linearLayout);

                                View view = new View(getBaseContext());
                                view.setLayoutParams(mHorizontalRuleLayoutParams);
                                view.setBackgroundColor(getResources().getColor(R.color.colorDark));
                                mTrailersLinearLayout.addView(view);

                                ++count;
                            }
                        }
                    }
                }
            }

            @Override @EverythingIsNonNull
            public void onFailure(Call<MovieTrailers> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void getReviews(int movieId) {
        MovieService movieService = RetroFitUtils.createMovieService();
        Call<MovieReviews> call = movieService.requestMovieReviews(movieId, BuildConfig.API_KEY_TMDB);
        call.enqueue(new Callback<MovieReviews>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<MovieReviews> call, Response<MovieReviews> response) {
                MovieReviews movieReviews = response.body();
                if (movieReviews != null) {
                    Review[] reviews = movieReviews.getReviews();
                    if (reviews != null) {
                        if (reviews.length > 0) mReviewsLinearLayout.setVisibility(View.VISIBLE);
                        for (int i = 0; i < reviews.length; ++i) {
                            final Review review = reviews[i];
                            LinearLayout linearLayout = new LinearLayout(getBaseContext());
                            linearLayout.setLayoutDirection(LinearLayout.HORIZONTAL);
                            linearLayout.setLayoutParams(LinearLayoutLayoutParams);
                            linearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse(review.getUrl());
                                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                }
                            });
                            TextView labelTextView = new TextView(getBaseContext());
                            labelTextView.setLayoutParams(mReviewLabelTextViewLayoutParams);
                            labelTextView.setText(getString(R.string.movie_detail_review_label, i + 1));
                            labelTextView.setGravity(Gravity.CENTER_VERTICAL);
                            linearLayout.addView(labelTextView);
                            TextView authorTextView = new TextView(getBaseContext());
                            authorTextView.setLayoutParams(mReviewAuthorTextViewLayoutParams);
                            authorTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                            authorTextView.setText(review.getAuthor());
                            authorTextView.setGravity(Gravity.CENTER_VERTICAL);
                            linearLayout.addView(authorTextView);
                            mReviewsLinearLayout.addView(linearLayout);

                            View view = new View(getBaseContext());
                            view.setLayoutParams(mHorizontalRuleLayoutParams);
                            view.setBackgroundColor(getResources().getColor(R.color.colorDark));
                            mReviewsLinearLayout.addView(view);
                        }
                    }
                }
            }

            @Override @EverythingIsNonNull
            public void onFailure(Call<MovieReviews> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void toggleFavorite() {
        mMovie.setIsFavorite(!mMovie.getIsFavorite());
        mMovieFavoriteButton.setBackground(getDrawable(
                mMovie.getIsFavorite() ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp
        ));
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mAppDatabase.movieDao().update(mMovie);
            }
        });
    }
}