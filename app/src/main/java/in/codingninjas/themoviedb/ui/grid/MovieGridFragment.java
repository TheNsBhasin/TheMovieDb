package in.codingninjas.themoviedb.ui.grid;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.data.Movie;
import in.codingninjas.themoviedb.data.MoviesService;
import in.codingninjas.themoviedb.data.SortHelper;
import in.codingninjas.themoviedb.ui.EndlessRecyclerViewOnScrollListener;
import in.codingninjas.themoviedb.ui.SortingDialogFragment;
import in.codingninjas.themoviedb.util.OnItemClickListener;

public class MovieGridFragment extends AbstractMoviesGridFragment {
    private static final String LOG_TAG = "MoviesGridFragment";
    private static final int SEARCH_QUERY_DELAY_MILLIS = 400;

    private MoviesService moviesService;
    private Handler handler;

    private EndlessRecyclerViewOnScrollListener endlessRecyclerViewOnScrollListener;
    private SearchView searchView;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MoviesService.BROADCAST_UPDATE_FINISHED)) {
                if (!intent.getBooleanExtra(MoviesService.EXTRA_IS_SUCCESSFUL_UPDATED, true)) {
                    Snackbar.make(swipeRefreshLayout, R.string.error_failed_to_update_movies, Snackbar.LENGTH_LONG).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                endlessRecyclerViewOnScrollListener.setLoading(false);
                updateGridLayout();
            } else if (action.equals(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED)){
                recyclerView.smoothScrollToPosition(0);
                movies.clear();
                getAdapter().notifyDataSetChanged();
                refreshMovies();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SortHelper sortHelper = SortHelper.getInstance(getContext());
        moviesService = MoviesService.getInstance(getContext(), sortHelper);
        moviesService.setOnDownloadCompleteListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MoviesService.BROADCAST_UPDATE_FINISHED);
        intentFilter.addAction(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
        if (endlessRecyclerViewOnScrollListener != null) {
            endlessRecyclerViewOnScrollListener.setLoading(moviesService.isLoading());
        }
        swipeRefreshLayout.setRefreshing(moviesService.isLoading());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_grid, menu);

        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        if (searchViewMenuItem != null) {
            searchView = (SearchView) searchViewMenuItem.getActionView();
            searchViewMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    recyclerView.setAdapter(null);
                    initMoviesGrid();
                    swipeRefreshLayout.setEnabled(true);
                    refreshMovies();
                    return true;
                }
            });

            setupSearchView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_show_sort_by_dialog) {
            showSortByDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDownloadComplete(ArrayList<Movie> moviesList) {
        movies.addAll(moviesList);
        getAdapter().notifyDataSetChanged();
        if (moviesList.size() == 0) {
            refreshMovies();
        }
        updateGridLayout();
    }

    @Override
    protected void onRefreshAction() {
        refreshMovies();
    }

    @Override
    protected void onMoviesGridInitialisationFinished() {
        endlessRecyclerViewOnScrollListener = new EndlessRecyclerViewOnScrollListener(getGridLayoutManager()) {
            @Override
            public void onLoadMore() {
                swipeRefreshLayout.setRefreshing(true);
                moviesService.loadMoreMovies();
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
    }

    private void setupSearchView() {
        if (searchView == null) {
            Log.e(LOG_TAG, "SearchView is not initialized");
            return;
        }

        handler = new Handler();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (query != null && !query.isEmpty()) {
                    moviesService.searchMovies(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String query) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (query != null && !query.isEmpty()) {
                            moviesService.searchMovies(query);
                        }
                    }
                }, SEARCH_QUERY_DELAY_MILLIS);
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setAdapter(null);
                recyclerView.removeOnScrollListener(endlessRecyclerViewOnScrollListener);
                updateGridLayout();
                swipeRefreshLayout.setEnabled(false);
            }
        });
    }

    @Override
    public void onSearchComplete(final ArrayList<Movie> moviesList) {
        MoviesAdapter mSearchAdapter = new MoviesAdapter(getContext(), moviesList, new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Movie movie = moviesList.get(position);
                if (getOnItemSelectedListener() != null) {
                    getOnItemSelectedListener().onItemSelected(movie);
                }
            }
        });
        recyclerView.setAdapter(mSearchAdapter);
        updateGridLayout();
    }

    private void refreshMovies() {
        swipeRefreshLayout.setRefreshing(true);
        movies.clear();
        getAdapter().notifyDataSetChanged();
        moviesService.refreshMovies();
    }

    private void showSortByDialog() {
        DialogFragment sortingDialogFragment = new SortingDialogFragment();
        sortingDialogFragment.show(getActivity().getSupportFragmentManager(), SortingDialogFragment.TAG);
    }

    public static int getMovieSize() {
        if (movies != null) {
            return movies.size();
        }
        return 0;
    }
}