package in.codingninjas.themoviedb.ui.detail;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.data.Movie;
import in.codingninjas.themoviedb.data.MovieReview;
import in.codingninjas.themoviedb.data.MovieVideo;
import in.codingninjas.themoviedb.network.MovieReviewsResponse;
import in.codingninjas.themoviedb.network.MovieVideosResponse;
import in.codingninjas.themoviedb.network.NetworkModule;
import in.codingninjas.themoviedb.network.TheMovieDbService;
import in.codingninjas.themoviedb.ui.ItemOffsetDecoration;
import in.codingninjas.themoviedb.util.Constants;
import in.codingninjas.themoviedb.util.OnItemClickListener;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MovieDetailFragment extends Fragment {
    private static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_IMAGE_SIZE = "w780";

    private static final double VOTE_PERFECT = 9.0;
    private static final double VOTE_GOOD = 7.0;
    private static final double VOTE_NORMAL = 5.0;

    @BindView(R.id.image_movie_detail_poster)
    ImageView movieImagePoster;

    @BindView(R.id.text_movie_original_title)
    TextView movieOriginalTitle;

    @BindView(R.id.text_movie_user_rating)
    TextView movieUserRating;

    @BindView(R.id.text_movie_release_date)
    TextView movieReleaseDate;

    @BindView(R.id.text_movie_overview)
    TextView movieOverview;

    @BindView(R.id.card_movie_detail)
    CardView cardMovieDetail;

    @BindView(R.id.card_movie_overview)
    CardView cardMovieOverview;

    @BindView(R.id.card_movie_videos)
    CardView cardMovieVideos;

    @BindView(R.id.movie_videos)
    RecyclerView movieVideos;

    @BindView(R.id.card_movie_reviews)
    CardView cardMovieReviews;

    @BindView(R.id.movie_reviews)
    RecyclerView movieReviews;

    private Movie movie;
    private MovieReviewsAdapter movieReviewsAdapter;
    private MovieVideosAdapter movieVideosAdapter;

    private TheMovieDbService theMovieDbService;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (movieVideosAdapter.getItemCount() == 0) {
            loadMovieVideos();
        }
        if (movieReviewsAdapter.getItemCount() == 0) {
            loadMovieReviews();
        }
        updateMovieVideosCard();
        updateMovieReviewsCard();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = (Movie) getArguments().getSerializable(Constants.MOVIE_INTENT_KEY);
            Log.i("onCreate", "getArguments != null");
        } else {
            Log.i("onCreate", "getArguments == null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        okhttp3.Cache cache = NetworkModule.providesOkHttpCache((Application) getContext().getApplicationContext());
        OkHttpClient okHttpClient = NetworkModule.providesOkHttpClient(cache);
        Retrofit retrofit = NetworkModule.providesRetrofit(okHttpClient);
        theMovieDbService = retrofit.create(TheMovieDbService.class);

        initViews();
        initVideosList();
        initReviewsList();
        setupCardsElevation();
        return rootView;
    }

    private void setupCardsElevation() {
        setupCardElevation(cardMovieDetail);
        setupCardElevation(cardMovieVideos);
        setupCardElevation(cardMovieOverview);
        setupCardElevation(cardMovieReviews);
    }

    private void setupCardElevation(View view) {
        ViewCompat.setElevation(view, convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
    }

    private void loadMovieVideos() {
        Call<MovieVideosResponse> responseCall = theMovieDbService.getMovieVideos(movie.getId(), Constants.API_KEY);
        responseCall.enqueue(new Callback<MovieVideosResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieVideosResponse> call, @NonNull Response<MovieVideosResponse> response) {
                if (response.isSuccessful()) {
                    MovieVideosResponse movieVideosResponse = response.body();
                    ArrayList<MovieVideo> movieVideosArrayList = movieVideosResponse.getResults();
                    movieVideosAdapter.setMovieVideos(movieVideosArrayList);
                    updateMovieVideosCard();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieVideosResponse> call, @NonNull Throwable t) {
                updateMovieVideosCard();
            }
        });
    }

    private void updateMovieVideosCard() {
        if (movieVideosAdapter == null || movieVideosAdapter.getItemCount() == 0) {
            cardMovieVideos.setVisibility(View.GONE);
        } else {
            cardMovieVideos.setVisibility(View.VISIBLE);
        }
    }

    private void loadMovieReviews() {
        Call<MovieReviewsResponse> responseCall = theMovieDbService.getMovieReviews(movie.getId(), Constants.API_KEY);
        responseCall.enqueue(new Callback<MovieReviewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieReviewsResponse> call, @NonNull Response<MovieReviewsResponse> response) {
                if (response.isSuccessful()) {
                    MovieReviewsResponse movieReviewsResponse = response.body();
                    ArrayList<MovieReview> movieReviewsArrayList = movieReviewsResponse.getResults();
                    movieReviewsAdapter.setMovieReviews(movieReviewsArrayList);
                    updateMovieReviewsCard();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieReviewsResponse> call, @NonNull Throwable t) {
                updateMovieReviewsCard();
            }
        });
    }

    private void updateMovieReviewsCard() {
        if (movieReviewsAdapter == null || movieReviewsAdapter.getItemCount() == 0) {
            cardMovieReviews.setVisibility(View.GONE);
        } else {
            cardMovieReviews.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        Picasso.with(getActivity().getApplicationContext())
                .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getPosterPath())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(movieImagePoster, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getActivity().getApplicationContext())
                                .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getPosterPath())
                                .error(R.drawable.no_poster)
                                .into(movieImagePoster);
                    }
                });
        movieOriginalTitle.setText(movie.getOriginalTitle());
        movieUserRating.setText(String.format(Locale.US, "%.1f", movie.getAverageVote()));
        movieUserRating.setTextColor(getRatingColor(movie.getAverageVote()));
        String releaseDate = String.format(getString(R.string.movie_detail_release_date),
                movie.getReleaseDate());
        movieReleaseDate.setText(releaseDate);
        movieOverview.setText(movie.getOverview());
    }

    private int getRatingColor(double averageVote) {
        if (averageVote >= VOTE_PERFECT) {
            return ContextCompat.getColor(getContext(), R.color.vote_perfect);
        } else if (averageVote >= VOTE_GOOD) {
            return ContextCompat.getColor(getContext(), R.color.vote_good);
        } else if (averageVote >= VOTE_NORMAL) {
            return ContextCompat.getColor(getContext(), R.color.vote_normal);
        } else {
            return ContextCompat.getColor(getContext(), R.color.vote_bad);
        }
    }

    private void initVideosList() {
        movieVideosAdapter = new MovieVideosAdapter(getActivity().getApplicationContext(), new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                onMovieVideoClicked(position);
            }
        });
        movieVideos.setAdapter(movieVideosAdapter);
        movieVideos.setItemAnimator(new DefaultItemAnimator());
        movieVideos.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.movie_item_offset));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        movieVideos.setLayoutManager(layoutManager);
    }

    private void initReviewsList() {
        movieReviewsAdapter = new MovieReviewsAdapter(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                onMovieReviewClicked(position);
            }
        });
        movieReviews.setAdapter(movieReviewsAdapter);
        movieReviews.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        movieReviews.setLayoutManager(layoutManager);
    }

    private void onMovieReviewClicked(int position) {
        MovieReview review = movieReviewsAdapter.getItem(position);
        if (review != null && review.getReviewUrl() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getReviewUrl()));
            startActivity(intent);
        }
    }

    private void onMovieVideoClicked(int position) {
        MovieVideo video = movieVideosAdapter.getItem(position);
        if (video != null && video.isYoutubeVideo()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_BASE_URL + video.getKey()));
            startActivity(intent);
        }
    }

    public float convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
