package in.codingninjas.themoviedb.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import in.codingninjas.themoviedb.data.MovieVideo;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MovieVideosResponse {
    @SerializedName("id")
    private long movieId;

    @SerializedName("results")
    private ArrayList<MovieVideo> results;

    public MovieVideosResponse(long movieId, ArrayList<MovieVideo> results) {
        this.movieId = movieId;
        this.results = results;
    }

    public long getMovieId() {
        return movieId;
    }

    public ArrayList<MovieVideo> getResults() {
        return results;
    }
}
