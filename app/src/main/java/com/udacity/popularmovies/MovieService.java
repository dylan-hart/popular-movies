package com.udacity.popularmovies;

import com.udacity.popularmovies.data.MovieDetails;
import com.udacity.popularmovies.data.MovieReviews;
import com.udacity.popularmovies.data.MovieTrailers;
import com.udacity.popularmovies.data.Page;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface MovieService {

    @GET("popular")
    Call<Page> requestPopularMovies(@Query("api_key")String apiKey);

    @GET("top_rated")
    Call<Page> requesetTopRatedMovies(@Query("api_key")String apiKey);

    @GET("{id}")
    Call<MovieDetails> requestMovieDetails(@Path("id")int id, @Query("api_key")String apiKey);

    @GET("{id}")
    Call<MovieTrailers> requestMovieTrailers(@Path("id")int id, @Query("api_key")String apiKey);

    @GET("{id}")
    Call<MovieReviews> requestMovieReviews(@Path("id")int id, @Query("api_key")String apiKey);
}
