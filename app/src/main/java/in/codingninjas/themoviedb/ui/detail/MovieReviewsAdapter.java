package in.codingninjas.themoviedb.ui.detail;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.data.MovieReview;
import in.codingninjas.themoviedb.util.OnItemClickListener;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewViewHolder> {
    private ArrayList<MovieReview> movieReviewArrayList;
    private OnItemClickListener onItemClickListener;

    public MovieReviewsAdapter(OnItemClickListener listener) {
        movieReviewArrayList = new ArrayList<>();
        this.onItemClickListener = listener;
    }

    public void setMovieReviews(@Nullable ArrayList<MovieReview> movieReviews) {
        this.movieReviewArrayList = movieReviews;
        notifyDataSetChanged();
    }

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie_review, parent, false);
        return new MovieReviewViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        MovieReview movieReview = movieReviewArrayList.get(position);
        holder.content.setText(movieReview.getContent());
        holder.author.setText(movieReview.getAuthor());
    }

    @Override
    public int getItemCount() {
        if (movieReviewArrayList == null) {
            return 0;
        }
        return movieReviewArrayList.size();
    }

    public MovieReview getItem(int position) {
        if (movieReviewArrayList == null || position < 0 || position > movieReviewArrayList.size()) {
            return null;
        }
        return movieReviewArrayList.get(position);
    }
}