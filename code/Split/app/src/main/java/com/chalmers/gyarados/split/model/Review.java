package com.chalmers.gyarados.split.model;

public class Review {


    private String reviewId;
    public enum Stars {
        ONE, TWO, THREE, FOUR, FIVE
    }
    private User user;
    private String reviewMsg;
    private Stars stars;
    private float floatStars;

    public Review(User user){
        this.user = user;
    }

    public Review(User user, Stars stars) {
        this.user = user;
        this.stars = stars;

    }

    public Review(User user, Stars stars, String reviewMsg ) {
        this.user = user;
        this.stars = stars;
        this.reviewMsg = reviewMsg;

    }

    public Review(User user, float floatStars, String reviewMsg ) {
        this.user = user;
        this.floatStars = floatStars;
        if (floatStars <= 1.01)
            this.stars = Stars.ONE;
        else if (floatStars <= 2.01)
            this.stars = Stars.TWO;
        else if (floatStars <= 3.01)
            this.stars = Stars.THREE;
        else if (floatStars <= 4.01)
            this.stars = Stars.FOUR;
        else if (floatStars <= 5.01)
            this.stars = Stars.FIVE;
        else
            System.out.println("Error: stars out of bound!");

        this.reviewMsg = reviewMsg;
    }

    public Review(User user, float floatStars) {
        this.user = user;
        this.floatStars = floatStars;
        if (floatStars <= 1.01)
            this.stars = Stars.ONE;
        else if (floatStars <= 2.01)
            this.stars = Stars.TWO;
        else if (floatStars <= 3.01)
            this.stars = Stars.THREE;
        else if (floatStars <= 4.01)
            this.stars = Stars.FOUR;
        else if (floatStars <= 5.01)
            this.stars = Stars.FIVE;
        else
            System.out.println("Error: stars out of bound!");
        this.reviewMsg = reviewMsg;
    }

    public void setFloatStars(float floatStars){
        this.floatStars = floatStars;
        if (floatStars <= 1.01)
            this.stars = Stars.ONE;
        else if (floatStars <= 2.01)
            this.stars = Stars.TWO;
        else if (floatStars <= 3.01)
            this.stars = Stars.THREE;
        else if (floatStars <= 4.01)
            this.stars = Stars.FOUR;
        else if (floatStars <= 5.01)
            this.stars = Stars.FIVE;

    }

    public void setStars(Stars nrOfStars) {
        this.stars = nrOfStars;
    }

    public void setReviewMsg(String reviewMsg) {
        this.reviewMsg = reviewMsg;
    }

    public Stars getStars() {
        return stars;
    }

    public User getUser() {
        return user;
    }

    public String getReviewMsg() {
        return reviewMsg;
    }

    public float getFloatStars() {
        return floatStars;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return user.equals(review.user);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}