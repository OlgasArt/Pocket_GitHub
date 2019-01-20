package com.example.android.pocketgithub;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

//created by OlgaS Art
// https://github.com/OlgasArt

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor. This class is only meant to hold static variables and methods.
     */

    private QueryUtils() {
    }

    /**
     * Return a list of {@link repo} objects that has been built up from
     * parsing a JSON response.
     */

    /**
     * Query the USGS dataset and return a list of {@link Event} objects.
     */
    public static List<Event> fetchRepoData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link repo}s
        List<Event> repos = extractFeatureFromJson(jsonResponse);
        // Return the list of {@link repo}s
        return repos;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the repository JSON results.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link Event} objects that has been built up from
     * parsing the given JSON response.
     */

    private static List<Event> extractFeatureFromJson(String repoJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(repoJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding repos to
        List<Event> repos = new ArrayList<>();

        try {
            // First, we have to create the “root” object that receives as input the entire string containing all the JSON response.
            //Get info for Items array*//
            JSONObject baseJsonResponse = new JSONObject(repoJSON);
            JSONArray repoArray = baseJsonResponse.getJSONArray("items");
            // Get a single repository at position i within the list of repositories
            for (int i = 0; i < repoArray.length(); i++) {
                JSONObject currentRepo = repoArray.getJSONObject(i);
                JSONObject owner = currentRepo.getJSONObject("owner");

                // Extract out the first feature (which is a name)
                JSONObject JSONRepos = repoArray.getJSONObject(i);
                String name = JSONRepos.getString("name");
                String description = JSONRepos.getString("full_name");
                String url = JSONRepos.getString("svn_url");
                String date = JSONRepos.getString("created_at");
                Double score = JSONRepos.getDouble("score");

                String image = owner.getString("avatar_url");

                // Create a new {@link Event} object
                Event repo = new Event(description, name, url, date, image, score);
                repos.add(repo);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the repository JSON results", e);


        }

        // Return the list of repositories
        return repos;
    }


}