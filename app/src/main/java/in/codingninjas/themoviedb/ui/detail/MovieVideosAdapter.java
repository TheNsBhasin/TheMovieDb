package in.codingninjas.themoviedb.ui.detail;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
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
import in.codingninjas.themoviedb.data.MovieVideo;
import in.codingninjas.themoviedb.util.OnItemClickListener;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideoViewHolder> {
    private static final String YOUTUBE_THUMBNAIL = "https://img.youtube.com/vi/%s/mqdefault.jpg";
    private final Context mContext;

    private ArrayList<MovieVideo> movieVideos;
    private OnItemClickListener onItemClickListener;

    public MovieVideosAdapter(Context mContext, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        movieVideos = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    public void setMovieVideos(@Nullable ArrayList<MovieVideo> movieVideos) {
        this.movieVideos = movieVideos;
        notifyDataSetChanged();
    }

    @Override
    public MovieVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_movie_video, parent, false);
        return new MovieVideoViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final MovieVideoViewHolder holder, int position) {
        if (movieVideos == null) {
            return;
        }
        final MovieVideo video = movieVideos.get(position);
        if (video.isYoutubeVideo()) {
            Picasso.with(mContext)
                    .load(String.format(YOUTUBE_THUMBNAIL, video.getKey()))
                    .placeholder(new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorAccent)))
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.movieVideoThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext)
                                    .load(String.format(YOUTUBE_THUMBNAIL, video.getKey()))
                                    .placeholder(new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorAccent)))
                                    .into(holder.movieVideoThumbnail);
                        }
                    });
        }
    }

    @Override
    public int getItemCount()
    {
        if (movieVideos == null) {
            return 0;
        }
        return movieVideos.size();
    }

    public MovieVideo getItem(int position) {
        if (movieVideos == null || position < 0 || position > movieVideos.size()) {
            return null;
        }
        return movieVideos.get(position);
    }
}