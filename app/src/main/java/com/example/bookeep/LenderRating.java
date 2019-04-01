package com.example.bookeep;

import java.util.ArrayList;

/**
 * Stores ratings and calculates overall ratings.
 * The UUID for a rating is the same as the user it is for.
 * @author Nolan Brost
 * @see User
 * @see Rating
 * @version 1.0.1
 */
public class LenderRating implements Rating {
    private ArrayList<Float> reviews;
    private float overallRating;
    private String uuid;                                    //is same as user for which the rating represents

    public LenderRating(){}

    /**
     * Construct a new LenderRating
     * @param uuid The UUID of the new LenderRating (same as the Lender user UUID)
     */
    public LenderRating(String uuid){
        reviews = new ArrayList<>();
        overallRating = (float) 0.0;
        this.uuid = uuid;

    }

    /**
     * Recalculate the overall rating based on all ratings and save it
     */
    public void recalculateRating(){
        if (reviews != null) {
            float total = 0;
            for (int i = 0; i < reviews.size(); i++) {
                total += reviews.get(i);
            }
            overallRating = (total / reviews.size());
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
