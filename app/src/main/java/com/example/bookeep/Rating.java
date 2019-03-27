package com.example.bookeep;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class will store the ratings and will calculate overall rating
 * The uuid for a rating will be the same as  the user it is for
 * @author Nolan Brost
 * @see User
 * @version 1.0.1
 */
public class Rating implements Serializable {
    private ArrayList<Integer> reviews;
    private Double ratingDouble;
    private String uuid;                                    //is same as user for which the rating represents

}
