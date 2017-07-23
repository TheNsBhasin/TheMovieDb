package in.codingninjas.themoviedb.ui.grid;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.util.OnItemClickListener;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MovieGridItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.image_movie_poster)
    ImageView moviePoster;

    @BindView(R.id.loader)
    ProgressBar progressBar;

    private OnItemClickListener onItemClickListener;

    public MovieGridItemViewHolder(View itemView, @Nullable OnItemClickListener onItemClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}