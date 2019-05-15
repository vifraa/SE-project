package com.chalmers.gyarados.split.model;

public class Review {

    private String reviewId;
    public enum Stars {
        ONE, TWO, THREE, FOUR, FIVE
    }
    private User user;
    private String reviewMsg;
    private Stars stars;

    public Review(String id, User user){
        this.reviewId = id;
        this.user = user;
    }

    public Review(String id, User user, Stars stars) {
        this.reviewId = id;
        this.user = user;
        this.stars = stars;

    }

    public Review(String id, User user, Stars stars, String reviewMsg ) {
        this.reviewId = id;
        this.user = user;
        this.stars = stars;
        this.reviewMsg = reviewMsg;

    }

    public void setId(String id) {
        this.reviewId = id;
    }

    public void setStars(Stars nrOfStars) {
        this.stars = nrOfStars;
    }

    public void setReviewMsg(String reviewMsg) {
        this.reviewMsg = reviewMsg;
    }

    public java.lang.String getId() {
        return reviewId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return reviewId.equals(review.reviewId) &&
                user.equals(review.user);
    }

    @Override
    public int hashCode() {
        return reviewId.hashCode();
    }
}