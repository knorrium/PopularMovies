package knorrium.info.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by felipek on 8/23/15.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "popularmovies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT UNIQUE NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL " +
                " );";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
