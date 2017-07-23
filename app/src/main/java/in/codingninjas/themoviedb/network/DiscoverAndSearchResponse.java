package in.codingninjas.themoviedb.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class DiscoverAndSearchResponse<T> {
    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private ArrayList<T> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private long totalResults;

    public DiscoverAndSearchResponse(int page, ArrayList<T> results, int totalPages, long totalResults) {
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public int getPage() {
        return page;
    }

    public List<T> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }
}