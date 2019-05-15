package gyarados.splitbackend.user;

import org.springframework.data.annotation.Id;

public class Review {

    private String comment;
    private Stars stars;

    private enum Stars {
        ONE, TWO, THREE, FOUR, FIVE
    }


    public Review(){

    }

    public Review(String comment, Stars stars){
        this.comment = comment;
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Stars getStars() {
        return stars;
    }

    public void setStars(Stars stars) {
        this.stars = stars;
    }
}
