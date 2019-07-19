package com.udacity.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.udacity.popularmovies.data.Movie;
import com.udacity.popularmovies.database.AppDatabase;

public class MainViewModel extends AndroidViewModel {

    private final static String LOG = MainViewModel.class.getSimpleName();

    private LiveData<Movie[]> favoriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        favoriteMovies = AppDatabase.getInstance(this.getApplication()).movieDao().loadFavorites();
    }

    public LiveData<Movie[]> getFavoriteMovies() {
        return favoriteMovies;
    }
}
