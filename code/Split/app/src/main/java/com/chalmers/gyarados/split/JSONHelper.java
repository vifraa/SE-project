package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.Group;
import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

//TODO should probably use something like
/*
Gson g = new Gson();
Group group = g.fromJson(jsonString, Group.class)

or with jackson

ObjectMapper objectMapper = new ObjectMapper();
Group group = objectMapper.readValue(jsonString,Group.class=

but right now we do it "manually"

 */

public class JSONHelper {

    private Gson gson;

    public JSONHelper() {
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
    }

    /**
     * Creates a jsoin string that represents a find group message
     * @param currentLatitude The current latitude
     * @param currentLongitude The current longitude
     * @param destinationLatitude The destination latitude
     * @param destinationLongitude The destination longitude
     * @return the json string
     */
    public String createFindGroupMessage(User user, Double currentLatitude, Double currentLongitude, Double destinationLatitude, Double destinationLongitude){
        JSONObject message = new JSONObject();
        try {
            if(user.getName()!=null){
                message.put("name",user.getName());
            }
            if(user.getUserId()!=null){
                message.put("userID",user.getUserId());
            }
            if(currentLatitude!=null){
                message.put("currentLatitude",currentLatitude);
            }
            if(currentLongitude!=null){
                message.put("currentLongitude",currentLongitude);
            }
            if(destinationLatitude!=null){
                message.put("destinationLatitude",destinationLatitude);
            }
            if(destinationLongitude!=null){
                message.put("destinationLongitude",destinationLongitude);
            }
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
