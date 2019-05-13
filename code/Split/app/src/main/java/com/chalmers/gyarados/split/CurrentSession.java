package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.User;

public class CurrentSession {
    private static User currentUser;
    private static Double currentLatitude;
    private static Double currentLongitude;
    private static Double desinationLatitude;
    private static Double destinationLongitude;
    private static int nrOfTravelers=1;

    public static Double getCurrentLatitude() {
        return currentLatitude;
    }

    public static void setCurrentLatitude(Double currentLatitude) {
        CurrentSession.currentLatitude = currentLatitude;
    }

    public static Double getCurrentLongitude() {
        return currentLongitude;
    }

    public static void setCurrentLongitude(Double currentLongitude) {
        CurrentSession.currentLongitude = currentLongitude;
    }

    public static Double getDestinationLatitude() {
        return desinationLatitude;
    }

    public static void setDesinationLatitude(Double desinationLatitude) {
        CurrentSession.desinationLatitude = desinationLatitude;
    }

    public static Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public static void setDestinationLongitude(Double destinationLongitude) {
        CurrentSession.destinationLongitude = destinationLongitude;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        CurrentSession.currentUser = currentUser;
    }

    public static int getNrOfTravelers() {
        return nrOfTravelers;
    }

    public static void setNrOfTravelers(int nrOfTravelers) {
        CurrentSession.nrOfTravelers = nrOfTravelers;
    }
}
