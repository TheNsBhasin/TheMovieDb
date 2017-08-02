package in.codingninjas.themoviedb.util;

import in.codingninjas.themoviedb.BuildConfig;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class Constants {
    public static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;
    public static String REQUEST_TOKEN = "";
    public static String SESSION_ID = "";
    public static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_IMAGE_SIZE = "w780";
    private static final String BACKDROP_IMAGE_SIZE = "w780";
    public static final String FETCH_BASE_URL = "http://api.themoviedb.org/3/";
    public static final String FETCH_BASE_MOVIE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String FETCH_BASE_AUTHENTICATION_URL = "https://www.themoviedb.org/authenticate/";
    public static final String FETCH_BASE_GAVATAR_URL = "https://www.gravatar.com/avatar/";
    public static final String ACTION_SUCCESS = "in.codingninjas.themoviedb.ui.login";
    public static final String SHARED_PREFERENCES = "in.codingninjas.themoviedb";
    public static final String POPULAR_MOVIES_LIST = "popular";
    public static final String TOP_RATED_MOVIES_LIST = "top_rated";
    public static final String MOST_RATED_MOVIES_LIST = "most_rated";
    public static final String MOVIE_INTENT_KEY = "movie_intent";
    public static final String SITE_YOUTUBE = "YouTube";
    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";

    public static String getTrailersUrl(long id){
        return FETCH_BASE_MOVIE_URL + id + "/videos?api_key=" + API_KEY;
    }

    public static String getReviewsUrl(long id){
        return FETCH_BASE_MOVIE_URL + id + "/reviews?api_key=" + API_KEY;
    }
}
