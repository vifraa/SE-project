package gyarados.splitbackend.user;

import org.springframework.data.annotation.Id;

public class User {

    private @Id String userID;
    private String name;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private int numberOfFriends;


    public User(){}

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



    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", currentLatitude='" + currentLatitude + '\'' +
                ", currentLongitude='" + currentLongitude + '\'' +
                ", destinationLatitude='" + destinationLatitude + '\'' +
                ", destinationLongitude='" + destinationLongitude + '\'' +
                '}';
    }

    public int getNumberOfFriends() {
        return numberOfFriends;
    }

    public void setNumberOfFriends(int numberOfFriends) {
        this.numberOfFriends = numberOfFriends;
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
    		 return name.equals(tempUser.name)
    	                && Double.compare(currentLatitude, tempUser.currentLatitude) == 0
    		 			&& Double.compare(currentLongitude, tempUser.currentLongitude) == 0
    		 			&& Double.compare(destinationLatitude, tempUser.destinationLatitude) == 0
    		 			&& Double.compare(destinationLongitude, tempUser.destinationLongitude) == 0
    		 			&& numberOfFriends - numberOfFriends == 0;
    }
}

