package in.codingninjas.themoviedb.network;

import in.codingninjas.themoviedb.data.Account;
import in.codingninjas.themoviedb.data.Movie;
import in.codingninjas.themoviedb.data.RequestToken;
import in.codingninjas.themoviedb.data.Session;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by nsbhasin on 21/07/17.
 */

public interface TheMovieDbService {
    @Headers({"authentication-callback"})

    @GET("movie/{category}")
    Call<DiscoverAndSearchResponse<Movie>> getMovies(@Path("category") String category, @Query("api_key") String api_key);

    @GET("movie/{id}/videos")
    Call<MovieVideosResponse> getMovieVideos(@Path("id") long movieId, @Query("api_key") String api_key);

    @GET("movie/{id}/reviews")
    Call<MovieReviewsResponse> getMovieReviews(@Path("id") long movieId, @Query("api_key") String api_key);

    @GET("discover/movie")
    Call<DiscoverAndSearchResponse<Movie>> discoverMovies(@Query("sort_by") String sortBy, @Query("page") Integer page, @Query("api_key") String api_key);

    @GET("search/movie")
    Call<DiscoverAndSearchResponse<Movie>> searchMovies(@Query("query") String query, @Query("page") Integer page, @Query("api_key") String api_key);

    @GET("authentication/token/new")
    Call<RequestToken> fetchRequestToken(@Query("api_key") String api_key);

    @GET("authentication/session/new")
    Call<Session> fetchSession(@Query("api_key") String api_key, @Query("request_token") String request_token);

    @GET("account")
    Call<Account> fetchAccount(@Query("api_key") String api_key, @Query("session_id") String session_id);

    @POST("account/{account_id}/favorite")
    Call<Void> addFavourate(@Query("api_key") String api_key, @Query("session_id") String session_id , @Query("media_type") String type, @Query("media_id") long id, @Query("favorite") boolean favourate);
}