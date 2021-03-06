package com.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Page implements Parcelable {
    @SerializedName("page")
    private int pageNumber;

    @SerializedName("total_result")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("results")
    private Movie[] movies;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pageNumber);
        dest.writeInt(totalResults);
        dest.writeInt(totalPages);
        dest.writeTypedArray(movies, 0);
    }

    private Page(Parcel in) {
        pageNumber = in.readInt();
        totalResults = in.readInt();
        totalPages = in.readInt();
        movies = in.createTypedArray(Movie.CREATOR);
    }

    public static final Creator<Page> CREATOR = new Creator<Page>() {
        public Page createFromParcel(Parcel source) {
            return new Page(source);
        }
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public Movie[] getMovies() {
        return movies;
    }

    public void setMovies(Movie[] movies) {
        this.movies = movies;
    }
}
