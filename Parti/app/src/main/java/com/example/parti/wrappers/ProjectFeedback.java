package com.example.parti.wrappers;

import java.math.BigInteger;


// Deprecated
public class ProjectFeedback {
    private BigInteger totalRating;
    private int numRatings;

    public void changeRating(int oldRating, int newRating) {
        totalRating =
                totalRating.add(BigInteger.valueOf(newRating)).subtract(BigInteger.valueOf(oldRating));
    }

    public void addRating(int rating) {
        totalRating = totalRating.add(BigInteger.valueOf(rating));
        numRatings++;
    }

    public double getAvgRating() {
        return totalRating.divide(BigInteger.valueOf(numRatings)).doubleValue();
    }
}
