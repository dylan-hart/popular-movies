package com.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class MovieDetails implements Parcelable {

    @SerializedName("budget")
    private int budget;

    @SerializedName("revenue")
    private int revenue;

    @SerializedName("runtime")
    private int runtime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(budget);
        dest.writeInt(revenue);
        dest.writeInt(runtime);
    }

    private MovieDetails(Parcel in) {
        budget = in.readInt();
        revenue = in.readInt();
        runtime = in.readInt();
    }

    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        public MovieDetails createFromParcel(Parcel source) {
            return new MovieDetails(source);
        }
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
}
