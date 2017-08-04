package in.codingninjas.themoviedb.data;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.codingninjas.themoviedb.network.DiscoverAndSearchResponse;
import in.codingninjas.themoviedb.network.TheMovieDbService;
import in.codingninjas.themoviedb.util.Constants;
import in.codingninjas.themoviedb.util.OnDownloadCompleteListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class FavoritesService {
    private static FavoritesService favoritesService;
    private static final Object LOCK = new Object();
    private OnDownloadCompleteListener mListener;
    private MoviesDatabase moviesDatabase;
    private MoviesDao moviesDao;
    private TheMovieDbService theMovieDbService;

    public static FavoritesService getInstance(Context context) {
        if (favoritesService == null) {
            synchronized (LOCK) {
                if (favoritesService == null) {
                    favoritesService = new FavoritesService(context);
                }
            }
        }
        return favoritesService;
    }

    public void setOnDownloadCompleteListener(OnDownloadCompleteListener listener) {
        mListener = listener;
    }

    private FavoritesService(Context context) {
        moviesDatabase = MoviesDatabase.getInstance(context);
        moviesDao = moviesDatabase.moviesDao();
        MoviesService moviesService = MoviesService.getInstance(context, null);
        theMovieDbService = moviesService.getTheMovieDbService();
    }

    public void addToFavorites(final Movie movie) {
        FavourateBody body = new FavourateBody("movie", movie.getId(), true);
        Call<Void> responseCall = theMovieDbService.addFavourate(Constants.API_KEY, Constants.SESSION_ID, body);
        responseCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("TAG", "onResponse: " + response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                moviesDao.insertMovie(movie);
                return null;
            }
        }.execute();
    }

    public void removeFromFavorites(final Movie movie) {
        FavourateBody body = new FavourateBody("movie", movie.getId(), false);
        Call<Void> responseCall = theMovieDbService.addFavourate(Constants.API_KEY, Constants.SESSION_ID, body);
        responseCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("TAG", "onResponse: " + response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("TAG", "onResponse: " + t.getCause());

            }
        });
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                moviesDao.deleteMovie(movie);
                return null;
            }
        }.execute();
    }

    public void getFavourates() {
        Call<DiscoverAndSearchResponse<Movie>> responseCall = theMovieDbService.getFavourateMovies(Constants.API_KEY, Constants.SESSION_ID);
        responseCall.enqueue(new Callback<DiscoverAndSearchResponse<Movie>>() {
            @Override
            public void onResponse(Call<DiscoverAndSearchResponse<Movie>> call, Response<DiscoverAndSearchResponse<Movie>> response) {
                if (response.isSuccessful()) {
                    DiscoverAndSearchResponse<Movie> discoverAndSearchResponse = response.body();
                    ArrayList<Movie> moviesArrayList = (ArrayList<Movie>) discoverAndSearchResponse.getResults();
                    mListener.onDownloadComplete(moviesArrayList);
                } else {
                    Log.i("responseCall", "Unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<DiscoverAndSearchResponse<Movie>> call, Throwable t) {
                new AsyncTask<Void, Void, List<Movie>>() {

                    @Override
                    protected List<Movie> doInBackground(Void... voids) {
                        return moviesDao.getAllMovies();
                    }

                    @Override
                    protected void onPostExecute(List<Movie> movies) {
                        super.onPostExecute(movies);
                        mListener.onDownloadComplete((ArrayList<Movie>) movies);
                    }
                }.execute();
            }
        });
    }

    public boolean isFavorite(Movie movie) {
        String Query = "SELECT * FROM movies WHERE id = " + movie.getId();
        Cursor cursor = moviesDatabase.query(Query, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}