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
    private ArrayList<Integer> reviews;
    private float overallRating;
    private String uuid;                                    //is same as user for which the rating represents

    public BorrowerRating(String uuid){
        reviews = new ArrayList<>();
        overallRating = (float) 0.0 ;
        this.uuid = uuid;

    }

    private void recalculateRating(){
        int total = 0;
        for (int i = 0; i < reviews.size() ; i++) {
            total += reviews.get(i);
        }
        overallRating = ((float) total)/ reviews.size();

    }

    public void addRating(Integer rating){
        reviews.add(rating);
        this.recalculateRating();
    }

    public float getRating(){
        return overallRating;
    }
    public Integer getNumRatings(){
        return reviews.size();
    }
}
