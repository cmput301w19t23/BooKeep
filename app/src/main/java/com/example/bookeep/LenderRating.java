package com.example.bookeep;

import java.util.ArrayList;

public class LenderRating implements Rating {
    private ArrayList<Integer> reviews;
    private float overallRating;
    private String uuid;                                    //is same as user for which the rating represents

    public LenderRating(String uuid){
        reviews = new ArrayList<>();
        overallRating = (float) 0.0;
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
