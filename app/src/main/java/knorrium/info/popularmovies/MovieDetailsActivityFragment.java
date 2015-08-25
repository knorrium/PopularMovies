package knorrium.info.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import knorrium.info.popularmovies.data.MoviesContract;
import knorrium.info.popularmovies.util.ImageHelper;

public class MovieDetailsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String DETAIL_URI = "detailUri";
    private static final int MOVIE_DETAIL_LOADER = 0;

    private static final String[] MOVIE_DETAILS_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_POPULARITY,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RATING
    };


    private static final int COLUMN_ID_INDEX = 0;
    private static final int COLUMN__ID_INDEX = 1;
    private static final int COLUMN_ORIGINAL_TITLE_INDEX = 2;
    private static final int COLUMN_OVERVIEW_INDEX = 3;
    private static final int COLUMN_POSTER_PATH_INDEX = 4;
    private static final int COLUMN_RELEASE_DATE_INDEX = 5;
    private static final int COLUMN_POPULARITY_INDEX = 6;
    private static final int COLUMN_VOTE_AVERAGE_INDEX = 7;

    private TextView mMovieTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mOverview;
    private ImageView mPosterThumbnail;

    private Uri mUri;

    public MovieDetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailsActivityFragment.DETAIL_URI);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        mVoteAverage = (TextView) rootView.findViewById(R.id.vote_average);
        mOverview = (TextView) rootView.findViewById(R.id.movie_overview);
        mPosterThumbnail = (ImageView) rootView.findViewById(R.id.poster);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_DETAILS_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mMovieTitle.setText(data.getString(COLUMN_ORIGINAL_TITLE_INDEX));
            mReleaseDate.setText(data.getString(COLUMN_RELEASE_DATE_INDEX).substring(0, 4));
            mVoteAverage.setText(String.format(getString(R.string.vote_average_format), data.getDouble(COLUMN_VOTE_AVERAGE_INDEX)));

            if (data.getString(COLUMN_OVERVIEW_INDEX) != null) {
                mOverview.setText(data.getString(COLUMN_OVERVIEW_INDEX));
            }

            ImageHelper.loadAsyncImage(
                    getActivity(),
                    mPosterThumbnail,
                    ImageHelper.IMAGE_SIZE_THUMBNAIL,
                    data.getString(COLUMN_POSTER_PATH_INDEX));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
