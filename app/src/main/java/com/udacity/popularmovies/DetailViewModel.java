package com.udacity.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import com.udacity.popularmovies.data.Movie;
import com.udacity.popularmovies.database.AppDatabase;

public class DetailViewModel extends AndroidViewModel {

    private final static String TAG = DetailViewModel.class.getSimpleName();

    private LiveData<Movie> detailMovie;

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    void setDetailMovieId(int id) {
        Log.d(TAG, "Querying the database.");
        detailMovie = AppDatabase.getInstance(this.getApplication()).movieDao().load(id);
    }

    LiveData<Movie> getDetailMovie() {
        return detailMovie;
    }
}
