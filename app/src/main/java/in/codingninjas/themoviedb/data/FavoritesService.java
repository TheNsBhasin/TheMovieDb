package in.codingninjas.themoviedb.data;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

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
    private MoviesService moviesService;
    private TheMovieDbService theMovieDbService;

    public static FavoritesService getInstance(Context context) {
        if(favoritesService == null){
            synchronized (LOCK){
                if(favoritesService == null){
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
        moviesService = MoviesService.getInstance(context, null);
        theMovieDbService = moviesService.getTheMovieDbService();
    }

    public void addToFavorites(final Movie movie) {
        Call<Void> responseCall = theMovieDbService.addFavourate(Constants.API_KEY,Constants.SESSION_ID , "movie", movie.getId(), true);
        responseCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

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
        Call<Void> responseCall = theMovieDbService.addFavourate(Constants.API_KEY, Constants.SESSION_ID, "movie", movie.getId(), false);
        responseCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

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

    public boolean isFavorite(Movie movie) {
        String Query = "SELECT * FROM movies WHERE id = " + movie.getId();
        Cursor cursor = moviesDatabase.query(Query, null);
        if(cursor.getCount()==0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}