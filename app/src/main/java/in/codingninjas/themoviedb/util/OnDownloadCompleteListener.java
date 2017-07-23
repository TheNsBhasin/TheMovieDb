package in.codingninjas.themoviedb.util;

import java.util.ArrayList;

import in.codingninjas.themoviedb.data.Movie;

/**
 * Created by nsbhasin on 21/07/17.
 */

public interface OnDownloadCompleteListener {
    void onDownloadComplete(ArrayList<Movie> moviesList);
    void onSearchComplete(ArrayList<Movie> moviesList);
}
