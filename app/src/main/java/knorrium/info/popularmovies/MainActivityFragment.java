package knorrium.info.popularmovies;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import knorrium.info.popularmovies.data.MoviesContract;
import knorrium.info.popularmovies.data.MoviesDbHelper;
import knorrium.info.popularmovies.sync.MoviesSyncAdapter;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER
    };

    public static final int COLUMN_ID_INDEX = 0;
    public static final int COLUMN_MOVIE_ID_INDEX = 1;
    public static final int COLUMN_POSTER_PATH_INDEX = 2;


    private MoviesAdapter mMoviesAdapter;
    private MovieListStateListener mListener;
    private GridView mPosterGrid;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mPosterGrid = (GridView) rootView.findViewById(R.id.poster_grid);
        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);
        mPosterGrid.setAdapter(mMoviesAdapter);

        mPosterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onMovieSelected(
                        MoviesContract.MovieEntry.buildMovieUri(mMoviesAdapter.getMovieId(position)));
            }
        });


        mPosterGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(
                    AbsListView view,
                    int firstVisibleItem,
                    int visibleItemCount,
                    int totalItemCount) {

//                if (firstVisibleItem > (totalItemCount - LOAD_MORE_THRESHOLD)) {
//                    SyncAdapter.loadMoreMoviesImmediately(getActivity());
//                }
            }
        });


        return rootView;


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MovieListStateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MovieListStateListener");
        }
    }

    public void updateSortOrder(String newSortOrder){
        mPosterGrid.scrollTo(0, 0);
        MoviesSyncAdapter.syncImmediately(getActivity());
        getActivity().invalidateOptionsMenu();

        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public interface MovieListStateListener {
        void onMovieSelected(Uri movieUri);
    }
}
