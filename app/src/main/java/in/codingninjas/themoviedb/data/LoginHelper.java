package in.codingninjas.themoviedb.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import in.codingninjas.themoviedb.util.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by nsbhasin on 23/07/17.
 */

public class LoginHelper {
    private static final String PREF_SESSION_KEY = "session";
    private static final String PREF_REQUEST_TOKEN = "request_token";

    private SharedPreferences sharedPreferences;
    private static LoginHelper loginHelper;
    private static final Object LOCK = new Object();

    public static LoginHelper getInstance(Context context) {
        if(loginHelper == null){
            synchronized (LOCK){
                if(loginHelper == null){
                    loginHelper = new LoginHelper(context);
                }
            }
        }
        return loginHelper;
    }

    private LoginHelper(Context context) {
        this.sharedPreferences = (context.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE));
    }

    public String getSessionByPreference() {
        String sessionId = sharedPreferences.getString(PREF_SESSION_KEY, "");
        return sessionId;
    }

    public String getTokenByPreference() {
        String requestToken = sharedPreferences.getString(PREF_REQUEST_TOKEN, "");
        return requestToken;
    }

    public boolean saveSessionByPreference(String sessionId, String requestToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SESSION_KEY, sessionId);
        editor.putString(PREF_REQUEST_TOKEN, requestToken);
        return editor.commit();
    }
}
