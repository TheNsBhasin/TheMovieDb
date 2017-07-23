package in.codingninjas.themoviedb.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by nsbhasin on 21/07/17.
 */

@Database(entities = Movie.class, version = 1)
abstract class MoviesDatabase extends RoomDatabase {
    private static MoviesDatabase moviesDatabase;
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "movies.db";

    static MoviesDatabase getInstance(Context context) {
        if (moviesDatabase == null) {
            synchronized (LOCK) {
                if (moviesDatabase == null) {
                    moviesDatabase = Room.databaseBuilder(context.getApplicationContext(), MoviesDatabase.class, DB_NAME).build();
                }
            }
        }
        return moviesDatabase;
    }

    abstract MoviesDao moviesDao();
}

