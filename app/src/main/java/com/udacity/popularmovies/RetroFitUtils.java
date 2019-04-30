package com.udacity.popularmovies;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Locale;

public class RetroFitUtils {
    private static final String URL_POPULAR_MOVIES = "http://api.themoviedb.org/3/movie/";

    public static MovieService createMovieService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_POPULAR_MOVIES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(MovieService.class);
    }

    public static String getPosterUrl(Context context, String posterPath) {
        String posterRez = context.getResources().getString(R.string.tmdb_thumbnail_resolution);
        return String.format(Locale.US, "http://image.tmdb.org/t/p/%s%s", posterRez, posterPath);
    }
}
