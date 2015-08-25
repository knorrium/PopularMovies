package knorrium.info.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import knorrium.info.popularmovies.util.ImageHelper;

public class MoviesAdapter extends CursorAdapter {

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        ImageHelper.loadAsyncImage(
                context,
                viewHolder.poster,
                ImageHelper.IMAGE_SIZE_NORMAL,
                cursor.getString(MainActivityFragment.COLUMN_POSTER_PATH_INDEX));
    }

    public long getMovieId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(MainActivityFragment.COLUMN_MOVIE_ID_INDEX);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private static class ViewHolder {
        public CustomPosterImageView poster;

        public ViewHolder(View view) {
            this.poster = (CustomPosterImageView) view;
        }
    }
}
