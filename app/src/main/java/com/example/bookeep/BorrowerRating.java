package com.example.bookeep;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class will store the ratings and will calculate overall rating
 * The uuid for a rating will be the same as  the user it is for
 * @author Nolan Brost
 * @see User
 * @see Rating
 * @version 1.0.1
 */
public class BorrowerRating implements Rating, Serializable {
    private ArrayList<Float> reviews;
    private float overallRating;
    private String uuid;                                    //is same as user for which the rating represents

    public BorrowerRating(){}
    /**
     * Construct a new BorrowerRating
     * @param uuid The UUID for the BorrowerRating, converted to String
     */
    public BorrowerRating(String uuid){
        reviews = new ArrayList<>();
        overallRating = (float) 0.0 ;
        this.uuid = uuid;

    }

    /**
     * Calculate the average rating across all reviews and save it
     */
    public void recalculateRating(){
        if (reviews != null) {
            float total = 0;
            for (int i = 0; i < reviews.size(); i++) {
                total += reviews.get(i);
            }
            overallRating = total / reviews.size();
        }

    }

    /**
     * Add a new rating to all of the reviews. Then recalculate the average rating.
     * @param rating The new rating to add.
     */
    public void addRating(float rating){
        if (reviews == null){
            reviews = new ArrayList<>();
        }
        reviews.add(rating);
        this.recalculateRating();
    }

    /**
     * Gets the average rating across all reviews.
     * @return The overall rating for all reviews of a BorrowerRating object.
     */
    public float getRating(){
        return overallRating;
    }

    /**
     * Gets the number of total ratings for this BorrowerRating object.
     * @return The size of the list of reviews/ratings.
     */
    public Integer getNumRatings(){
        if (reviews == null){
            return 0;
        }
        return reviews.size();
    }

    public ArrayList<Float> getReviews(){return reviews;}
}
