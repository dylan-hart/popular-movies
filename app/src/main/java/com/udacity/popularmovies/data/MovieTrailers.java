package com.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class MovieTrailers implements Parcelable {

    @SerializedName("results")
    private Trailer[] trailers;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(trailers, 0);
    }

    private MovieTrailers(Parcel in) {
        trailers = in.createTypedArray(Trailer.CREATOR);
    }

    public static final Creator<MovieTrailers> CREATOR = new Creator<MovieTrailers>() {
        @Override
        public MovieTrailers createFromParcel(Parcel source) {
            return new MovieTrailers(source);
        }

        @Override
        public MovieTrailers[] newArray(int size) {
            return new MovieTrailers[size];
        }
    };

    public Trailer[] getTrailers() {
        return trailers;
    }
}
