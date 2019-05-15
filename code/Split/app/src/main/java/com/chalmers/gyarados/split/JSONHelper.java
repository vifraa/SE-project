package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.Group;
import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

;

import org.json.JSONException;
import org.json.JSONObject;




public class JSONHelper {

    private Gson gson;

    public JSONHelper() {
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    }

    /**
     * Creates a jsoin string that represents a find group message
     *
     * @return the json string
     */
    public String createFindGroupMessage(){
        JSONObject message = new JSONObject();
        User currentUser = CurrentSession.getCurrentUser();
        try {
            if(currentUser.getName()!=null){
                message.put("name",currentUser.getName());
            }
            if(currentUser.getUserId()!=null){
                message.put("userID",currentUser.getUserId());
            }
            if(CurrentSession.getCurrentLatitude()!=null){
                message.put("currentLatitude",CurrentSession.getCurrentLatitude());
            }
            if(CurrentSession.getCurrentLongitude()!=null){
                message.put("currentLongitude",CurrentSession.getCurrentLongitude());
            }
            if(CurrentSession.getDestinationLatitude()!=null){
                message.put("destinationLatitude",CurrentSession.getDestinationLatitude());
            }
            if(CurrentSession.getDestinationLongitude()!=null){
                message.put("destinationLongitude",CurrentSession.getDestinationLongitude());
            }
            if(CurrentSession.getCurrentPhotoUri()!=null){
                message.put("photoUrl",CurrentSession.getCurrentPhotoUri());
            }

            message.put("numberOfTravelers",CurrentSession.getNrOfTravelers());
        } catch (JSONException e) {
            return null;
        }
        return message.toString();

    }

    /**
     * Converts json string representing a chat message to an actual message
     * @param messageInJson The message as a json string
     * @return The actual message
     */
    public Message convertJsonToChatMessage(String messageInJson) {
        return gson.fromJson(messageInJson,Message.class);
    }



    /**
     * Converts json string representing a group to an actual group
     * @param jsonGroup The message as a json string
     * @return The group
     */
    public Group convertJsonToGroup(String jsonGroup){
        return gson.fromJson(jsonGroup,Group.class);
    }

    public String convertChatMessageToJSon(Message message) {
        return gson.toJson(message);
    }


}
