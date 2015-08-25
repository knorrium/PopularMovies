package knorrium.info.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import knorrium.info.popularmovies.R;
import knorrium.info.popularmovies.util.Utility;
import knorrium.info.popularmovies.data.MoviesContract;

public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;

        try {
            //Network operation based on https://gist.github.com/udacityandroid/d6a7bb21904046a91695
            final String BASE_URL = getContext().getString(R.string.api_movie_entrypoint);
            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, getContext().getString(R.string.api_key))
                    .appendQueryParameter(SORT_BY_PARAM, Utility.getPreferredSortOrder(getContext()))
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            movieJsonStr = buffer.toString();
            getMovieDataFromJson(movieJsonStr);


        }catch (IOException e) {
            Log.e(LOG_TAG, "Error " + e.getMessage(), e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void getMovieDataFromJson(String movieJsonStr) throws JSONException {
        JSONObject moviesJson = new JSONObject(movieJsonStr);

        try {
            JSONArray moviesArray = moviesJson.getJSONArray("results");

            Vector<ContentValues> contentValues = new Vector<>(moviesArray.length());

            for (int i = 0; i < moviesArray.length(); i++) {

                long movieId;
                String originalTitle;
                String overview;
                String posterPath;
                String releaseDate;
                double voteAverage;
                double popularity;

                JSONObject movie = moviesArray.getJSONObject(i);
                movieId = movie.getLong("id");
                originalTitle = movie.getString(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE);
                overview = movie.getString(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
                popularity = movie.getDouble(MoviesContract.MovieEntry.COLUMN_MOVIE_POPULARITY);
                posterPath = movie.getString(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER);
                voteAverage = movie.getDouble(MoviesContract.MovieEntry.COLUMN_MOVIE_RATING);
                releaseDate = movie.getString(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, originalTitle);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, overview);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER, posterPath);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RATING, voteAverage);

                contentValues.add(movieValues);
            }

            if (contentValues.size() > 0) {
                // Delete everything before we insert again
                getContext().getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,
                        null,
                        null);

                ContentValues[] cvArray = new ContentValues[contentValues.size()];
                contentValues.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(
                        MoviesContract.MovieEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), false);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

}
