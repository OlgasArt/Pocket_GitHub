package com.example.android.pocketgithub;

import android.graphics.drawable.Icon;


//created by OlgaS Art
// https://github.com/OlgasArt

public class Event {

    /**
     * Name of the repository
     */
    public final String mName;
    /**
     * Description of the repository
     */
    public final String mDescription;
    /**
     * Score for the repository
     */
    public final Double mScore;
    /**
     * Owner image of the repository
     */
    public String mImage;
    /**
     * URL of the repository
     */
    private String mUrl;
    /**
     * Date created of the repository
     */
    private String mDate;


    /**
     * Constructs a new {@link Event} object.
     *
     * @param name        is the name of the repository
     * @param description is the dscription of the repository
     * @param url         is the website URL to find more details about the repository
     * @param date        is the date of creation of repository
     * @param image       is the owner of the repository image
     * @param score       is the total score of the repository
     */

    public Event(String description, String name, String url, String date, String image, Double score) {
        mName = name;
        mDescription = description;
        mUrl = url;
        mDate = date;
        mImage = image;
        mScore = score;
    }

    /**
     * Returns the URL of the Repo.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns the name of the Repo.
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the description of the Repo.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns the image of the owner of the Repo.
     */
    public String getImage() {
        return mImage;
    }

    /**
     * Returns the created date of the Repo.
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Returns the score of the Repo.
     */
    public Double getScore() {
        return mScore;
    }

}



