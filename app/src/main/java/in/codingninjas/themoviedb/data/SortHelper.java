package in.codingninjas.themoviedb.data;

import android.content.Context;
import android.content.SharedPreferences;

import in.codingninjas.themoviedb.util.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class SortHelper {
    private static final String PREF_SORT_BY_KEY = "sortBy";
    private static final String PREF_SORT_BY_DEFAULT_VALUE = "popularity.desc";

    private SharedPreferences sharedPreferences;
    private static SortHelper sortHelper;
    private static final Object LOCK = new Object();

    public static SortHelper getInstance(Context context) {
        if(sortHelper == null){
            synchronized (LOCK){
                if(sortHelper == null){
                    sortHelper = new SortHelper(context);
                }
            }
        }
        return sortHelper;
    }

    private SortHelper(Context context) {
        this.sharedPreferences = (context.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE));
    }

    public Sort getSortByPreference() {
        String sort = sharedPreferences.getString(PREF_SORT_BY_KEY, PREF_SORT_BY_DEFAULT_VALUE);
        return Sort.fromString(sort);
    }

    public String getSortedMovies() {
        Sort sort = getSortByPreference();
        switch (sort) {
            case MOST_POPULAR:
                return Constants.POPULAR_MOVIES_LIST;
            case TOP_RATED:
                return Constants.TOP_RATED_MOVIES_LIST;
            case MOST_RATED:
                return Constants.MOST_RATED_MOVIES_LIST;
            default:
                throw new IllegalStateException("Unknown sort.");
        }
    }

    public boolean saveSortByPreference(Sort sort) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SORT_BY_KEY, sort.toString());
        return editor.commit();
    }
}
