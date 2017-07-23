package in.codingninjas.themoviedb.ui.grid;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.data.Movie;
import in.codingninjas.themoviedb.util.Constants;
import in.codingninjas.themoviedb.util.OnItemClickListener;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MovieGridItemViewHolder> {
    private Context mContext;
    private ArrayList<Movie> movies;
    private OnItemClickListener onItemClickListener;

    public MoviesAdapter(Context mContext, ArrayList<Movie> movies, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.movies = movies;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MovieGridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movie, parent, false);
        return new MovieGridItemViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final MovieGridItemViewHolder holder, int position) {
        Movie movie = movies.get(position);
        final String url = Constants.MOVIE_POSTER_BASE_URL + Constants.POSTER_IMAGE_SIZE + movie.getPosterPath();
        holder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(mContext)
                .load(url)
                .fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.moviePoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.GONE);
                        Picasso.with(mContext)
                                .load(url)
                                .fit()
                                .error(ContextCompat.getDrawable(mContext, R.drawable.no_poster))
                                .into(holder.moviePoster);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }
}