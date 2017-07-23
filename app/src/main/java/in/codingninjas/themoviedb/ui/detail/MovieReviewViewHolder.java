package in.codingninjas.themoviedb.ui.detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.util.OnItemClickListener;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class MovieReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.text_movie_review_content)
    TextView content;

    @BindView(R.id.text_movie_review_author)
    TextView author;

    private OnItemClickListener onItemClickListener;

    public MovieReviewViewHolder(View itemView, OnItemClickListener onItemClickListener) {
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