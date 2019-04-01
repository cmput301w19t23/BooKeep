package com.example.bookeep;

/**
 * This interface will be used by BorrowerRating and LenderRating
 * @author Nolan Brost
 * @see BorrowerRating
 * @see LenderRating
 * @version 1.0.1
 */
public interface Rating {
    void addRating(float rating);
    float getRating();
    Integer getNumRatings();
    void recalculateRating();
}
