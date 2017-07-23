package in.codingninjas.themoviedb.data;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

import in.codingninjas.themoviedb.network.DiscoverAndSearchResponse;
import in.codingninjas.themoviedb.network.NetworkModule;
import in.codingninjas.themoviedb.network.TheMovieDbService;
import in.codingninjas.themoviedb.ui.grid.MovieGridFragment;
import in.codingninjas.themoviedb.util.Constants;
import in.codingninjas.themoviedb.util.OnDownloadCompleteListener;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MoviesService {
    public static final String BROADCAST_UPDATE_FINISHED = "UpdateFinished";
    public static final String EXTRA_IS_SUCCESSFUL_UPDATED = "isSuccessfulUpdated";

    private static final int PAGE_SIZE = 20;
    private SortHelper sortHelper;
    private volatile boolean loading = false;

    private TheMovieDbService theMovieDbService;
    private OkHttpClient okHttpClient;
    private LocalBroadcastManager localBroadcastManager;
    private static MoviesService moviesService;
    private Retrofit retrofit;
    private static final Object LOCK = new Object();

    private OnDownloadCompleteListener mListener;

    public void setOnDownloadCompleteListener(OnDownloadCompleteListener listener) {
        mListener = listener;
    }

    public static MoviesService getInstance(Context context, SortHelper sortHelper) {
        if(moviesService == null){
            synchronized (LOCK){
                if(moviesService == null){
                    moviesService = new MoviesService(context, sortHelper);
                }
            }
        }
        return moviesService;
    }

    private MoviesService(Context context, SortHelper sortHelper) {
        okhttp3.Cache cache = NetworkModule.providesOkHttpCache((Application) context.getApplicationContext());
        okHttpClient = NetworkModule.providesOkHttpClient(cache);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        retrofit = NetworkModule.providesRetrofit(okHttpClient);
        theMovieDbService = retrofit.create(TheMovieDbService.class);
        this.sortHelper = sortHelper;
    }

    public TheMovieDbService getTheMovieDbService() {
        return theMovieDbService;
    }

    public void refreshMovies() {
        if (loading) {
            return;
        }
        loading = true;

        String sort = sortHelper.getSortByPreference().toString();
        callDiscoverMovies(sort, null);
    }

    public boolean isLoading() {
        return loading;
    }

    public void loadMoreMovies() {
        if (loading) {
            return;
        }
        loading = true;
        String sort = sortHelper.getSortByPreference().toString();
        String urlString = sortHelper.getSortedMovies();
        if (urlString == null) {
            return;
        }
        callDiscoverMovies(sort, getCurrentPage() + 1);
    }

    public void searchMovies(String query) {
        if (loading) {
            return;
        }
        callSearchMovies(query);
    }

    private void callDiscoverMovies(String sort, @Nullable Integer page) {
        Call<DiscoverAndSearchResponse<Movie>> responseCall = theMovieDbService.discoverMovies(sort, page, Constants.API_KEY);
        responseCall.enqueue(new Callback<DiscoverAndSearchResponse<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<DiscoverAndSearchResponse<Movie>> call, @NonNull Response<DiscoverAndSearchResponse<Movie>> response) {
                if (response.isSuccessful()) {
                    DiscoverAndSearchResponse<Movie> discoverAndSearchResponse = response.body();
                    ArrayList<Movie> moviesArrayList = (ArrayList<Movie>) discoverAndSearchResponse.getResults();
                    onDownloadComplete(moviesArrayList);
                } else {
                    Log.i("responseCall", "Unsuccessful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DiscoverAndSearchResponse<Movie>> call, Throwable t) {
                loading = false;
                sendUpdateFinishedBroadcast(false);
            }
        });
    }

    private void onDownloadComplete(ArrayList<Movie> moviesArrayList) {
        if (mListener != null) {
            loading = false;
            mListener.onDownloadComplete(moviesArrayList);
            sendUpdateFinishedBroadcast(true);
        }
    }

    private void callSearchMovies(String query) {
        Call<DiscoverAndSearchResponse<Movie>> responseCall = theMovieDbService.searchMovies(query, null, Constants.API_KEY);
        responseCall.enqueue(new Callback<DiscoverAndSearchResponse<Movie>>() {
            @Override
            public void onResponse(Call<DiscoverAndSearchResponse<Movie>> call, Response<DiscoverAndSearchResponse<Movie>> response) {
                if (response.isSuccessful()) {
                    DiscoverAndSearchResponse<Movie> discoverAndSearchResponse = response.body();
                    ArrayList<Movie> moviesArrayList = (ArrayList<Movie>) discoverAndSearchResponse.getResults();
                    onSearchComplete(moviesArrayList);
                } else {
                    Log.i("responseCall", "Unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<DiscoverAndSearchResponse<Movie>> call, Throwable t) {
                loading = false;
            }
        });
    }

    private void onSearchComplete(ArrayList<Movie> moviesArrayList) {
        if (mListener != null) {
            loading = false;
            mListener.onSearchComplete(moviesArrayList);
            sendUpdateFinishedBroadcast(true);
        }
    }

    private int getCurrentPage() {
        int currentPage = 1;
        int movieSize = MovieGridFragment.getMovieSize();
        if (movieSize != 0) {
            currentPage = (movieSize - 1) / PAGE_SIZE + 1;
        }
        return currentPage;
    }

    private void sendUpdateFinishedBroadcast(boolean successfulUpdated) {
        Intent intent = new Intent(BROADCAST_UPDATE_FINISHED);
        intent.putExtra(EXTRA_IS_SUCCESSFUL_UPDATED, successfulUpdated);
        localBroadcastManager.sendBroadcast(intent);
    }
}
