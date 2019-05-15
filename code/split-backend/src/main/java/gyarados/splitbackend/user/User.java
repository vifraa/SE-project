package gyarados.splitbackend.user;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class User {

    private @Id String userID;
    private String name;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;

    private List<Review> reviews;
    private String photoUrl;


    public User(){
        reviews = new ArrayList<>();
    }


    private int numberOfTravelers;


    public boolean addReview(Review review){
        return reviews.add(review);
    }

    @Override
    public boolean equals (Object user) {
    		//if the object is compared to itself, return true
    		if(user == this) {
    			return true;
    			}
    		// Check if user is an instance of User or not
    		if (!(user instanceof User)) { 
    	            return false; 
    	        }
    		 
    		//Typecast User so that we can compare data members
    		 User tempUser = (User) user;
    		 
    		 //Compare data members and return accordingly
    		 return userID.equals(tempUser.userID);

    }

    // GETTERS AND SETTERS

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", currentLatitude='" + currentLatitude + '\'' +
                ", currentLongitude='" + currentLongitude + '\'' +
                ", destinationLatitude='" + destinationLatitude + '\'' +
                ", destinationLongitude='" + destinationLongitude + '\'' +
                ", numberOfTravelers='" + numberOfTravelers + '\'' +
                '}';
    }

    public int getNumberOfTravelers() {
        return numberOfTravelers;
    }

    public void setNumberOfTravelers(int numberOfTravelers) {
        this.numberOfTravelers = numberOfTravelers;
    }



}

