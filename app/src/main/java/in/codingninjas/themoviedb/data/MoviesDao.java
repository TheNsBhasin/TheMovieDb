package in.codingninjas.themoviedb.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by nsbhasin on 21/07/17.
 */

@Dao
interface MoviesDao {
    @Query("SELECT * FROM movies")
    List<Movie> getAllMovies();

    @Insert
    void insertMovie(Movie... movies);

    @Delete
    void deleteMovie(Movie movie);
}