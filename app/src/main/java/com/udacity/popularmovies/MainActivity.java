package com.udacity.popularmovies;

import android.app.SharedElementCallback;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
import com.udacity.popularmovies.database.AppDatabase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();
    private final static String SAVED_SELECTED_MOVIE = "SELECTED_MOVIE";
    private final static String SAVED_MOVIE_TYPE = "MOVIE_TYPE";
    private final static String SAVED_POPULAR_MOVIES = "POPULAR_MOVIES";
    private final static String SAVED_TOP_RATED_MOVIES = "TOP_RATED_MOVIES";
    private final static String SAVED_RECYCLER_LAYOUT = "RECYCLER_LAYOUT";

    enum MovieType {
        UNINITIALIZED,
        MOST_POPULAR,
        TOP_RATED,
        FAVORITE
    }

    private RecyclerView mRecyclerView;
    private final MoviesAdapter mMoviesAdapter = new MoviesAdapter();
    private int mViewPosition = 0;
    private Movie[] mPopularMovies;
    private Movie[] mTopRatedMovies;
    private MovieType mMovieType = MovieType.UNINITIALIZED;
    private AppDatabase mAppDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppDatabase = AppDatabase.getInstance(getApplicationContext());

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
                        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                        if (layoutManager != null) {
                            View view = layoutManager.findViewByPosition(mViewPosition);
                            if (view != null) {
                                sharedElements.put(names.get(0), view);
                            }
                        }
                    }
                }
            });

            Parcelable savedLayout = savedInstanceState.getParcelable(SAVED_RECYCLER_LAYOUT);
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.onRestoreInstanceState(savedLayout);
            }

            mMovieType = (MovieType)savedInstanceState.getSerializable(SAVED_MOVIE_TYPE);
            mPopularMovies = (Movie[])savedInstanceState.getParcelableArray(SAVED_POPULAR_MOVIES);
            mTopRatedMovies = (Movie[])savedInstanceState.getParcelableArray(SAVED_TOP_RATED_MOVIES);

            if (mMovieType == MovieType.MOST_POPULAR) mMoviesAdapter.setMovieData(mPopularMovies);
            else if (mMovieType == MovieType.TOP_RATED) mMoviesAdapter.setMovieData(mTopRatedMovies);
            else if (mMovieType == MovieType.FAVORITE) showFavorites();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_SELECTED_MOVIE, mViewPosition);
        outState.putSerializable(SAVED_MOVIE_TYPE, mMovieType);
        outState.putParcelableArray(SAVED_POPULAR_MOVIES, mPopularMovies);
        outState.putParcelableArray(SAVED_TOP_RATED_MOVIES, mTopRatedMovies);
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            outState.putParcelable(SAVED_RECYCLER_LAYOUT, layoutManager.onSaveInstanceState());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if (mMovieType == MovieType.MOST_POPULAR) {
            menu.findItem(R.id.action_sort_by_most_popular).setChecked(true);
        } else if (mMovieType == MovieType.TOP_RATED) {
            menu.findItem(R.id.action_sort_by_highest_rated).setChecked(true);
        } else if (mMovieType == MovieType.FAVORITE) {
            menu.findItem(R.id.action_sort_by_favorite).setChecked(true);
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
            case R.id.action_sort_by_highest_rated:
                item.setChecked(true);
                showTopRatedMovies();
                return true;
            case R.id.action_sort_by_favorite:
                item.setChecked(true);
                showFavorites();
                return true;
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
                    public void onResponse(@NonNull Call<Page> call, @NonNull Response<Page> response) {
                        Page page = response.body();
                        if (page != null) {
                            mPopularMovies = page.getMovies();
                            Executors.newSingleThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mAppDatabase.movieDao().insert(mPopularMovies);
                                }
                            });
                            mMoviesAdapter.setMovieData(mPopularMovies);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Page> call, @NonNull Throwable t) {
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
                mMoviesAdapter.setMovieData(mTopRatedMovies);
            } else {
                MovieService movieService = RetroFitUtils.createMovieService();
                Call<Page> call = movieService.requesetTopRatedMovies(BuildConfig.API_KEY_TMDB);
                call.enqueue(new Callback<Page>() {
                    @Override
                    public void onResponse(@NonNull Call<Page> call, @NonNull Response<Page> response) {
                        Page page = response.body();
                        if (page != null) {
                            mTopRatedMovies = page.getMovies();
                            Executors.newSingleThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mAppDatabase.movieDao().insert(mTopRatedMovies);
                                }
                            });
                            mMoviesAdapter.setMovieData(mTopRatedMovies);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Page> call, @NonNull Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
        }
    }

    private void showFavorites() {
        if (mMovieType != MovieType.FAVORITE) {
            mMovieType = MovieType.FAVORITE;
        }
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavoriteMovies().observe(this, new Observer<Movie[]>() {
            @Override
            public void onChanged(@Nullable Movie[] movies) {
                if (mMovieType == MovieType.FAVORITE) {
                    mMoviesAdapter.setMovieData(movies);
                }
            }
        });
    }
}
