package in.codingninjas.themoviedb.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import in.codingninjas.themoviedb.data.MovieReview;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MovieReviewsResponse {
    @SerializedName("id")
    private long movieId;

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private ArrayList<MovieReview> results;

    @SerializedName("total_pages")
    private int totalPages;

    public MovieReviewsResponse(long movieId, int page, ArrayList<MovieReview> results, int totalPages) {
        this.movieId = movieId;
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
    }

    public long getMovieId() {
        return movieId;
    }

    public int getPage() {
        return page;
    }

    public ArrayList<MovieReview> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
