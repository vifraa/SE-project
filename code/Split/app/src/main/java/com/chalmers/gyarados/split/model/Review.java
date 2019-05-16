package com.chalmers.gyarados.split.model;

public class Review {


    public enum Stars {
        ONE, TWO, THREE, FOUR, FIVE
    }
    private User user;
    private String comment;
    private Stars stars;
    private float floatStars;

    public Review(User user){
        this.user = user;
    }

    public Review(User user, Stars stars) {
        this.user = user;
        this.stars = stars;

    }

    public Review(User user, Stars stars, String comment) {
        this.user = user;
        this.stars = stars;
        this.comment = comment;

    }

    public Review(User user, float floatStars, String comment) {
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

        this.comment = comment;
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
        this.comment = comment;
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

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Stars getStars() {
        return stars;
    }

    public User getUser() {
        return user;
    }

    public String getComment() {
        return comment;
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