package in.codingninjas.themoviedb.ui.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.data.LoginHelper;
import in.codingninjas.themoviedb.data.MoviesService;
import in.codingninjas.themoviedb.data.RequestToken;
import in.codingninjas.themoviedb.data.Session;
import in.codingninjas.themoviedb.network.TheMovieDbService;
import in.codingninjas.themoviedb.ui.MainActivity;
import in.codingninjas.themoviedb.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String BROADCAST_SESSION_PREFERENCE_CHANGED = "SessionPreferenceChanged";

    @BindView(R.id.web_view)
    WebView webView;

    private LoginHelper loginHelper;
    private TheMovieDbService theMovieDbService;
    private RequestToken requestToken;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "BroadcastReceiver");

            Call<Session> responseCall = theMovieDbService.fetchSession(Constants.API_KEY, requestToken.getRequestToken());
            responseCall.enqueue(new Callback<Session>() {
                @Override
                public void onResponse(@NonNull Call<Session> call, @NonNull Response<Session> response) {
                    if (response.isSuccessful()) {
                        Session session = response.body();
                        loginHelper.saveSessionByPreference(session.getSessionId(), requestToken.getRequestToken());
                        Constants.REQUEST_TOKEN = requestToken.getRequestToken();
                        Constants.SESSION_ID = session.getSessionId();
                        sendSessionPreferenceChangedBroadcast();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Session> call, @NonNull Throwable t) {
                    Log.i("BroadcastReceiver", "Unsuccessful");
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        MoviesService moviesService = MoviesService.getInstance(this, null);
        theMovieDbService = moviesService.getTheMovieDbService();
        loginHelper = LoginHelper.getInstance(this);

        setupWebView();
        setupActionBar();
        fetchRequestToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_SUCCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                setupActionBar();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("onPageFinished", url);
                if (url.endsWith("allow")) {
                    Intent intent = new Intent(Constants.ACTION_SUCCESS);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    Log.i("onPageFinished",Constants.ACTION_SUCCESS);
                    return;
                }
                setupActionBar();
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                setupActionBar();
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
            getSupportActionBar().setTitle("Sign In");
            if (webView.getUrl() != null) {
                Log.i(TAG, webView.getUrl());
                getSupportActionBar().setSubtitle(webView.getUrl());
            }
        }
    }

    public void fetchRequestToken() {
        Call<RequestToken> responseCall = theMovieDbService.fetchRequestToken(Constants.API_KEY);
        responseCall.enqueue(new Callback<RequestToken>() {
            @Override
            public void onResponse(@NonNull Call<RequestToken> call, @NonNull Response<RequestToken> response) {
                if (response.isSuccessful()) {
                    requestToken = response.body();
                    if (requestToken != null) {
                        loadWebpage(requestToken.getRequestToken());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RequestToken> call, @NonNull Throwable t) {
                Log.i("fetchRequestToken", "Unsuccessful");
            }
        });
    }

    private void loadWebpage(String requestToken) {
        Log.i(TAG, Constants.FETCH_BASE_AUTHENTICATION_URL + requestToken);
        webView.loadUrl(Constants.FETCH_BASE_AUTHENTICATION_URL + requestToken);

    }

    private void sendSessionPreferenceChangedBroadcast() {
        Intent intent = new Intent(BROADCAST_SESSION_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Log.i("SessionPreference",BROADCAST_SESSION_PREFERENCE_CHANGED + " sent");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            setupActionBar();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
