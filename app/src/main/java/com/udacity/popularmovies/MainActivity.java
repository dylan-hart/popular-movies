package com.udacity.popularmovies;

import android.app.SharedElementCallback;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.udacity.popularmovies.data.Movie;
import com.udacity.popularmovies.data.Page;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();
    public final static String EXTRA_TRANSITION_NAME = "TRANSITION_NAME";
    private final static String SAVED_SELECTED_MOVIE = "SELECTED_MOVIE";
    private final static String SAVED_MOVIE_TYPE = "MOVIE_TYPE";
    private final static String SAVED_POPULAR_MOVIES = "POPULAR_MOVIES";
    private final static String SAVED_TOP_RATED_MOVIES = "TOP_RATED_MOVIES";
    private final static String SAVED_RECYCLER_LAYOUT = "RECYCLER_LAYOUT";

    enum MovieType {
        UNINITIALIZED,
        MOST_POPULAR,
        TOP_RATED
    }

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter = new MoviesAdapter();
    private int mViewPosition = 0;
    private Movie[] mPopularMovies;
    private Movie[] mTopRatedMovies;
    private MovieType mMovieType = MovieType.UNINITIALIZED;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_posters);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMoviesAdapter);
        int span = getResources().getInteger(R.integer.movie_grid_span);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, span));

        if (savedInstanceState == null) {
            showPopularMovies();
        } else {
            mViewPosition = savedInstanceState.getInt(SAVED_SELECTED_MOVIE);
            setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (sharedElements.isEmpty()) {
                        View view = mRecyclerView.getLayoutManager().findViewByPosition(mViewPosition);
                        if (view != null) {
                            sharedElements.put(names.get(0), view);
                        }
                    }
                }
            });

            Parcelable savedLayout = savedInstanceState.getParcelable(SAVED_RECYCLER_LAYOUT);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedLayout);

            mMovieType = savedInstanceState.getParcelable(SAVED_MOVIE_TYPE);
            mPopularMovies = savedInstanceState.getParcelableArrayList(SAVED_POPULAR_MOVIES);
            mTopRatedMovies = savedInstanceState.getParcelableArrayList(SAVED_TOP_RATED_MOVIES);

            if (mMovieType == MovieType.MOST_POPULAR) mMoviesAdapter.setMovieData(mPopularMovies);
            else if (mMovieType == MovieType.TOP_RATED) mMoviesAdapter.setMovieData(mTopRatedMovies);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_SELECTED_MOVIE, mViewPosition);
        outState.putParcelable(SAVED_MOVIE_TYPE, mMovieType);
        outState.putParcelableArrayList(SAVED_POPULAR_MOVIES, mPopularMovies);
        outState.putParcelableArrayList(SAVED_TOP_RATED_MOVIES, mTopRatedMovies);
        outState.putParcelable(SAVED_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if (mMovieType == MovieType.MOST_POPULAR) {
            menu.findItem(R.id.action_sort_by_most_popular).setChecked(true);
        } else if (mMovieType == MovieType.TOP_RATED) {
            menu.findItem(R.id.action_sort_by_highest_rated).setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_most_popular:
                item.setChecked(true);
                showPopularMovies();
                return true;
                break;
            case R.id.action_sort_by_highest_rated:
                item.setChecked(true);
                showTopRatedMovies();
                return true;
                break;
            default:
                return false;
        }
    }

    private void showPopularMovies() {
        if (mMovieType != MovieType.MOST_POPULAR) {
            mMovieType = MovieType.MOST_POPULAR;
            if (mPopularMovies != null) {
                mMoviesAdapter.setMovieData(mPopularMovies);
            } else {
                MovieService movieService = RetroFitUtils.createMovieService();
                Call<Page> call = movieService.requestPopularMovies(BuildConfig.API_KEY_TMDB);
                call.enqueue(new Callback<Page>() {
                    @Override
                    public void onResponse(Call<Page> call, Response<Page> response) {
                        mPopularMovies = response.body().getMovies();
                        mMoviesAdapter.setMovieData(mPopularMovies);
                    }

                    @Override
                    public void onFailure(Call<Page> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
        }
    }

    private void showTopRatedMovies() {
        if (mMovieType != MovieType.TOP_RATED) {
            mMovieType = MovieType.TOP_RATED;
            if (mTopRatedMovies != null) {
                mMoviesAdapter.setMovieData(mPopularMovies);
            } else {
                MovieService movieService = RetroFitUtils.createMovieService();
                Call<Page> call = movieService.requestPopularMovies(BuildConfig.API_KEY_TMDB);
                call.enqueue(new Callback<Page>() {
                    @Override
                    public void onResponse(Call<Page> call, Response<Page> response) {
                        mTopRatedMovies = response.body().getMovies();
                        mMoviesAdapter.setMovieData(mTopRatedMovies);
                    }

                    @Override
                    public void onFailure(Call<Page> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
        }
    }
}
