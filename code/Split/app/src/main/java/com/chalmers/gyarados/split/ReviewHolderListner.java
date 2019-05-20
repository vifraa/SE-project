package com.chalmers.gyarados.split;


interface ReviewHolderListner {

    void feedbackRecieved(String user, String feedback);

    void ratingRecieved(String user, float rating);
}
