package com.example.android.pocketgithub;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//created by OlgaS Art
// https://github.com/OlgasArt

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Event>> {


    //** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    /**
     * URL to query  - search all repositories created after 01-01-2019 and display projects made in javascript,
     * limit to 100 results sorted by date updated in ascending order
     */
    private static final String REQUEST_URL =
            "https://api.github.com/search/repositories?q=%20created:%3E2019-01-01+language:javascrpt&per_page=100&sort=updated&order=asc";
    /**
     * Constant value for the repository loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int REPO_LOADER_ID = 1;
    ListView reposListView;
    /**
     * RepoAdapter for the list of repositories
     */
    RepoAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        reposListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        reposListView.setEmptyView(mEmptyStateTextView);
        // Create a new adapter that takes an empty list of repos as input
        mAdapter = new RepoAdapter(this, new ArrayList<Event>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        reposListView.setAdapter(mAdapter);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected repository.
        reposListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current repository that was clicked on
                Event currentRepo = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri repoUri = Uri.parse(currentRepo.getUrl());

                // Create a new intent to view the repository URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, repoUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(REPO_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }


    @Override
    public Loader<List<Event>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new RepoLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> repos) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No repos found."
        mEmptyStateTextView.setText(R.string.no_repositories);


        // If there is a valid list of {@link repo}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (repos != null && !repos.isEmpty()) {
            mAdapter.addAll(repos);
            // updateUi(repos);
        }
    }

    //Searching functionality, menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem listItem = menu.findItem(R.id.search_list);
        SearchView searchView = (SearchView) listItem.getActionView();
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        // return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        // RepoLoader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}


