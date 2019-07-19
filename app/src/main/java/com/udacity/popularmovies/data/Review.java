package com.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Review implements Parcelable {

    @SerializedName("author")
    private String author;

//    @SerializedName("content")
//    private String content;

    @SerializedName("url")
    private String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
//        dest.writeString(content);
        dest.writeString(url);
    }

    private Review(Parcel in) {
        author = in.readString();
//        content = in.readString();
        url = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAuthor() {
        return author;
    }

//    public String getContent() {
//        return content;
//    }

    public String getUrl() {
        return url;
    }
}
