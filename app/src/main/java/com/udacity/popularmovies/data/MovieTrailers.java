package com.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class MovieTrailers implements Parcelable {

    @SerializedName("key")
    private String key;

    @SerializedName("site")
    private String site;

    @SerializedName("type")
    private String type;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(site);
        dest.writeString(type);
    }

    private MovieTrailers(Parcel in) {
        key = in.readString();
        site = in.readString();
        type = in.readString();
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

    public String getKey() {
        return key;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }
}
