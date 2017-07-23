package in.codingninjas.themoviedb.ui.grid;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import in.codingninjas.themoviedb.data.FavoritesService;
import in.codingninjas.themoviedb.data.Movie;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class FavoritesGridFragment extends AbstractMoviesGridFragment {
    private FavoritesService favoritesService;

    @Override
    public void onResume() {
        super.onResume();
        favoritesService.getFavourates();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoritesService = FavoritesService.getInstance(this.getContext());
        favoritesService.setOnDownloadCompleteListener(this);
        favoritesService.getFavourates();
    }

    @Override
    public void onDownloadComplete(ArrayList<Movie> moviesList) {
        movies.clear();
        getAdapter().notifyDataSetChanged();
        movies.addAll(moviesList);
        updateGridLayout();
    }

    @Override
    public void onSearchComplete(ArrayList<Movie> moviesList) {

    }

    @Override
    protected void onRefreshAction() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onMoviesGridInitialisationFinished() {
        favoritesService.getFavourates();
    }
}
