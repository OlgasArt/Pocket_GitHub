package com.example.android.pocketgithub;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

//created by OlgaS Art
// https://github.com/OlgasArt

public class RepoLoader extends AsyncTaskLoader<List<Event>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = RepoLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link RepoLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public RepoLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Event> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of repositories.
        List<Event> repos = QueryUtils.fetchRepoData(mUrl);
        return repos;
    }
}