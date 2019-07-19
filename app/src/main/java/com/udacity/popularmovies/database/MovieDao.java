package com.udacity.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;
import com.udacity.popularmovies.data.Movie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie WHERE isFavorite = 1")
    LiveData<Movie[]> loadFavorites();

    @Query("SELECT * FROM movie WHERE id=:id")
    LiveData<Movie> load(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Movie... movies);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Movie movie);

    @Delete
    void delete(Movie movie);
}
