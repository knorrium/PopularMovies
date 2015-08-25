package knorrium.info.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import knorrium.info.popularmovies.sync.MoviesSyncAdapter;
import knorrium.info.popularmovies.util.Utility;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.MovieListStateListener {

    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoviesSyncAdapter.initializeSyncAdapter(this);
        MoviesSyncAdapter.syncImmediately(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String preferredSortOrder = Utility.getPreferredSortOrder(this);

        if (preferredSortOrder != null && !preferredSortOrder.equals(mSortOrder)) {
            MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_poster_grid);
            if ( null != fragment ) {
                fragment.updateSortOrder(mSortOrder);
            }
            mSortOrder = preferredSortOrder;
        }
    }


    @Override
    public void onMovieSelected(Uri movieUri) {
        openMovieDetailActivity(movieUri);
    }

    private void openMovieDetailActivity(Uri movieUri) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);

        if (movieUri != null) {
            intent.setData(movieUri);
        }

        startActivity(intent);
    }
}
