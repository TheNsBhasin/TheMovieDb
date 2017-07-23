package in.codingninjas.themoviedb.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.data.Account;
import in.codingninjas.themoviedb.data.FavoritesService;
import in.codingninjas.themoviedb.data.LoginHelper;
import in.codingninjas.themoviedb.data.Movie;
import in.codingninjas.themoviedb.data.MoviesService;
import in.codingninjas.themoviedb.data.Session;
import in.codingninjas.themoviedb.data.SortHelper;
import in.codingninjas.themoviedb.network.TheMovieDbService;
import in.codingninjas.themoviedb.ui.detail.MovieDetailActivity;
import in.codingninjas.themoviedb.ui.detail.MovieDetailFragment;
import in.codingninjas.themoviedb.ui.grid.FavoritesGridFragment;
import in.codingninjas.themoviedb.ui.grid.MovieGridFragment;
import in.codingninjas.themoviedb.ui.login.LoginActivity;
import in.codingninjas.themoviedb.util.Constants;
import in.codingninjas.themoviedb.util.OnItemSelectedListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {
    private static final String SELECTED_MOVIE_KEY = "MovieSelected";
    private static final String SELECTED_NAVIGATION_ITEM_KEY = "SelectedNavigationItem";

    @Nullable
    @BindView(R.id.movie_detail_container)
    ScrollView movieDetailContainer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    View mNavigationHeaderView;
    TextView mUsernameTextView;
    ImageView mUserImageView;

    private SortHelper sortHelper;
    private LoginHelper loginHelper;
    private FavoritesService favoritesService;
    private TheMovieDbService theMovieDbService;
    private boolean twoPaneMode;
    private Movie selectedMovie = null;
    private int selectedNavigationItem;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("MainActivity", action);
            if (action.equals(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED)) {
                hideMovieDetailContainer();
                updateTitle();
            }
            if (action.equals(LoginActivity.BROADCAST_SESSION_PREFERENCE_CHANGED)) {
                changeAvatar();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_grid_container, new MovieGridFragment())
                    .commit();
        }

        twoPaneMode = (movieDetailContainer != null);
        if (twoPaneMode && selectedMovie == null) {
            if (savedInstanceState == null) {
                movieDetailContainer.setVisibility(View.GONE);
            }
        }
        sortHelper = SortHelper.getInstance(this);
        loginHelper = LoginHelper.getInstance(this);
        MoviesService moviesService = MoviesService.getInstance(this, sortHelper);
        theMovieDbService = moviesService.getTheMovieDbService();
        favoritesService = FavoritesService.getInstance(MainActivity.this);

        setupToolbar();
        setupNavigationDrawer();
        setupNavigationView();
        setupFab();
        changeAvatar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        intentFilter.addAction(LoginActivity.BROADCAST_SESSION_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
        updateTitle();
        Log.i("intentFilter", SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED + "recieved");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void updateTitle() {
        if (selectedNavigationItem == 0) {
            String[] sortTitles = getResources().getStringArray(R.array.pref_sort_by_labels);
            int currentSortIndex = sortHelper.getSortByPreference().ordinal();
            Log.i("currentSortIndex", "" + currentSortIndex);
            String title = Character.toString(sortTitles[currentSortIndex].charAt(0)).toUpperCase(Locale.US) + sortTitles[currentSortIndex].substring(1);
            Log.i("title", title);
            setTitle(title);
        } else if (selectedNavigationItem == 1) {
            setTitle(getResources().getString(R.string.favorites_grid_title));
        }
    }

    private void setupFab() {
        if (fab != null) {
            if (twoPaneMode && selectedMovie != null) {
                if (favoritesService.isFavorite(selectedMovie)) {
                    fab.setImageResource(R.drawable.ic_favorite_white);
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_white_border);
                }
                fab.show();
            } else {
                fab.hide();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable(SELECTED_MOVIE_KEY, selectedMovie);
        outState.putInt(SELECTED_NAVIGATION_ITEM_KEY, selectedNavigationItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            selectedMovie = (Movie) savedInstanceState.getSerializable(SELECTED_MOVIE_KEY);
            selectedNavigationItem = savedInstanceState.getInt(SELECTED_NAVIGATION_ITEM_KEY);
            Menu menu = navigationView.getMenu();
            menu.getItem(selectedNavigationItem).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (twoPaneMode && movieDetailContainer != null) {
            Log.i("onItemSelected", "Bundle Sent");
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.MOVIE_INTENT_KEY, movie);
            movieDetailFragment.setArguments(bundle);
            movieDetailContainer.setVisibility(View.VISIBLE);
            selectedMovie = movie;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, movieDetailFragment)
                    .commit();
            setupFab();
        } else {
            Log.i("onItemSelected", "Intent Sent");
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra(Constants.MOVIE_INTENT_KEY, movie);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        int id = item.getItemId();
        if (id == R.id.drawer_item_explore) {
            if (selectedNavigationItem != 0) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_grid_container, new MovieGridFragment())
                        .commit();
                selectedNavigationItem = 0;
                hideMovieDetailContainer();
            }
            drawer.closeDrawers();
            updateTitle();
            return true;
        } else if (id == R.id.drawer_item_favorites) {
            if (selectedNavigationItem != 1) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_grid_container, new FavoritesGridFragment())
                        .commit();
                selectedNavigationItem = 1;
                hideMovieDetailContainer();
            }
            drawer.closeDrawers();
            updateTitle();
            return true;
        } else if (id == R.id.drawer_item_signout) {
            loginHelper.saveSessionByPreference("", "");
            item.setChecked(false);
            changeAvatar();
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void onFabClicked(View view) {
        if (favoritesService.isFavorite(selectedMovie)) {
            favoritesService.removeFromFavorites(selectedMovie);
            showSnackbar(R.string.message_removed_from_favorites);
            if (selectedNavigationItem == 1) {
                hideMovieDetailContainer();
            }
        } else {
            favoritesService.addToFavorites(selectedMovie);
            showSnackbar(R.string.message_added_to_favorites);
        }
        setupFab();
    }

    public void onConnectClicked(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void showSnackbar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackbar(@StringRes int messageResourceId) {
        showSnackbar(getString(messageResourceId));
    }

    private void hideMovieDetailContainer() {
        selectedMovie = null;
        setupFab();
        if (twoPaneMode && movieDetailContainer != null) {
            movieDetailContainer.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
        mNavigationHeaderView = navigationView.getHeaderView(0);
        mUsernameTextView = mNavigationHeaderView.findViewById(R.id.text_username_nav);
        mUserImageView = mNavigationHeaderView.findViewById(R.id.image_user_nav);
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void changeAvatar() {
        Log.i("changeAvatar", "on changeAvatar");
        final String requestToken = loginHelper.getTokenByPreference();
        String sessionId = loginHelper.getSessionByPreference();
        if (!requestToken.isEmpty()) {
            Log.i("changeAvatar", requestToken);
            Call<Session> responseCall = theMovieDbService.fetchSession(Constants.API_KEY, requestToken);
            responseCall.enqueue(new Callback<Session>() {
                @Override
                public void onResponse(@NonNull Call<Session> call, @NonNull Response<Session> response) {
                    if (response.isSuccessful()) {
                        Session session = response.body();
                        assert session != null;
                        loginHelper.saveSessionByPreference(session.getSessionId(), requestToken);
                        Log.i("changeAvatar", session.getSessionId() +  "   " + requestToken);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Session> call, @NonNull Throwable t) {
                    Log.i("BroadcastReceiver", "Unsuccessful");
                }
            });
        }
        if (requestToken.isEmpty() || sessionId.isEmpty()){
            Picasso.with(this)
                    .load(R.drawable.logo)
                    .into(mUserImageView);
            mUsernameTextView.setText(R.string.connect_to_tmdb);
            mUsernameTextView.setClickable(true);
            return;
        }
        Constants.REQUEST_TOKEN = requestToken;
        Constants.SESSION_ID = sessionId;
        Call<Account> responseCall = theMovieDbService.fetchAccount(Constants.API_KEY, sessionId);
        responseCall.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(@NonNull Call<Account> call, @NonNull Response<Account> response) {
                Account account = response.body();
                final String hash = account.getAvatar().getGravatar().getHash();
                String username = account.getUsername();
                mUsernameTextView.setText(username);
                mUsernameTextView.setClickable(false);
                Picasso.with(MainActivity.this)
                        .load(Constants.FETCH_BASE_GAVATAR_URL + hash)
                        .transform(new CircleTransform())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mUserImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(MainActivity.this)
                                        .load(Constants.FETCH_BASE_GAVATAR_URL + hash + "?s=200")
                                        .transform(new CircleTransform())
                                        .error(R.drawable.logo)
                                        .into(mUserImageView);
                            }
                        });
            }

            @Override
            public void onFailure(@NonNull Call<Account> call, @NonNull Throwable t) {
                mUsernameTextView.setText(R.string.connect_to_tmdb);
                mUsernameTextView.setClickable(true);
                Log.i("onFailure", "unsuccessful");
            }
        });
    }
}
