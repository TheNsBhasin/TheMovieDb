package in.codingninjas.themoviedb.ui.detail;

import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.data.FavoritesService;
import in.codingninjas.themoviedb.data.Movie;
import in.codingninjas.themoviedb.util.Constants;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String BACKDROP_IMAGE_SIZE = "w780";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.backdrop_image)
    ImageView movieBackdropImage;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    private Movie movie;
    private FavoritesService favoritesService;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        movie = (Movie) getIntent().getSerializableExtra(Constants.MOVIE_INTENT_KEY);
        if (movie != null) {
            Log.i("MovieDetailActivity", "movie != null");
        } else {
            Log.i("MovieDetailActivity", "movie == null");
        }

        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.MOVIE_INTENT_KEY ,movie);
        movieDetailFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.movies_grid_container, movieDetailFragment)
                .commit();

        favoritesService = FavoritesService.getInstance(this);

        initAppbar();
        initToolbar();
        ViewCompat.setElevation(nestedScrollView, convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
        ViewCompat.setElevation(fab, convertDpToPixel(getResources().getInteger(R.integer.movie_detail_fab_elevation_in_dp)));
        updateFab();
    }

    public void onFabClicked(View view) {
        if (favoritesService.isFavorite(movie)) {
            favoritesService.removeFromFavorites(movie);
            showSnackbar(R.string.message_removed_from_favorites);
        } else {
            favoritesService.addToFavorites(movie);
            showSnackbar(R.string.message_added_to_favorites);
        }
        updateFab();
    }

    private void updateFab() {
        if (favoritesService.isFavorite(movie)) {
            fab.setImageResource(R.drawable.ic_favorite_white);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_white_border);
        }
    }

    private void initAppbar() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShown = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(movie.getTitle());
                    isShown = true;
                } else if(isShown) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShown = false;
                }
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            setTitle("");
            Picasso.with(this)
                    .load(Constants.MOVIE_POSTER_BASE_URL + BACKDROP_IMAGE_SIZE + movie.getBackdropPath())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(movieBackdropImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.i("Picasso", "onSuccess");
                        }

                        @Override
                        public void onError() {
                            Log.i("Picasso", "onError");
                            Picasso.with(MovieDetailActivity.this)
                                    .load(Constants.MOVIE_POSTER_BASE_URL + BACKDROP_IMAGE_SIZE + movie.getBackdropPath())
                                    .into(movieBackdropImage);
                        }
                    });
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackbar(@StringRes int messageResourceId) {
        showSnackbar(getString(messageResourceId));
    }

    public float convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
