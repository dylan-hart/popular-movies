package com.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class MovieReviews implements Parcelable {

    @SerializedName("results")
    private Review[] reviews;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(reviews, 0);
    }

    private MovieReviews(Parcel in) {
        reviews = in.createTypedArray(Review.CREATOR);
    }

    public static final Creator<MovieReviews> CREATOR = new Creator<MovieReviews>() {
        @Override
        public MovieReviews createFromParcel(Parcel source) {
            return new MovieReviews(source);
        }

        @Override
        public MovieReviews[] newArray(int size) {
            return new MovieReviews[size];
        }
    };

    public Review[] getReviews() {
        return reviews;
    }
}
