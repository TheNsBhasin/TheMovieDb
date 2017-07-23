package in.codingninjas.themoviedb.ui.grid;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.data.Movie;
import in.codingninjas.themoviedb.ui.ItemOffsetDecoration;
import in.codingninjas.themoviedb.util.OnDownloadCompleteListener;
import in.codingninjas.themoviedb.util.OnItemClickListener;
import in.codingninjas.themoviedb.util.OnItemSelectedListener;

public abstract class AbstractMoviesGridFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener, OnDownloadCompleteListener {
    @BindView(R.id.movies_grid)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.view_no_movies)
    RelativeLayout noMoviesView;

    private MoviesAdapter mAdapter;
    private OnItemSelectedListener onItemSelectedListener;
    private GridLayoutManager gridLayoutManager;

    protected static ArrayList<Movie> movies;

    public AbstractMoviesGridFragment() {
        // Required empty public constructor
    }

    protected abstract void onRefreshAction();

    protected abstract void onMoviesGridInitialisationFinished();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            onItemSelectedListener = (OnItemSelectedListener) context;
        } else {
            throw new IllegalStateException("The activity must implement " + OnItemSelectedListener.class.getSimpleName() + " interface.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        ButterKnife.bind(this, rootView);

        initSwipeRefreshLayout();
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.movie_item_offset));
        initMoviesGrid();

        onRefresh();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGridLayout();
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return onItemSelectedListener;
    }

    protected void updateGridLayout() {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noMoviesView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMoviesView.setVisibility(View.GONE);
        }
    }

    protected void initMoviesGrid() {
        movies = new ArrayList<>();
        mAdapter = new MoviesAdapter(getContext(), movies, new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Movie movie = movies.get(position);
                onItemSelectedListener.onItemSelected(movie);
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int columns = getResources().getInteger(R.integer.movies_columns);
        gridLayoutManager = new GridLayoutManager(getActivity(), columns);
        recyclerView.setLayoutManager(gridLayoutManager);
        onMoviesGridInitialisationFinished();
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
    }

    @Override
    public void onItemClick(View itemView, int position) {
        onItemSelectedListener.onItemSelected(mAdapter.getItem(position));
    }

    @Override
    public void onRefresh() {
        onRefreshAction();
    }

    public MoviesAdapter getAdapter() {
        return mAdapter;
    }

    public GridLayoutManager getGridLayoutManager() {
        return gridLayoutManager;
    }
}
