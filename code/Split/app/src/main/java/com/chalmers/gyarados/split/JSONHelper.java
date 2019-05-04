package com.chalmers.gyarados.split;

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


public class JSONHelper {
    /**
     * Creates a json string that represents a chat message
     * @param sender The name of the sender
     * @param content The content of the message
     * @param type The type of message
     * @return the json string
     */
    public String createChatMessage(String sender, String content, String type){
        JSONObject message = new JSONObject();
        try {
            if(sender!=null){

                message.put("sender",sender);
            }
            if(content!=null){
                message.put("content",content);
            }
            if(type!=null){
                message.put("type",type);
            }
        } catch (JSONException e) {
            return null;
        }
        return message.toString();

    }

    /**
     * Creates a jsoin string that represents a find group message
     * @param name The name of the one looking for a group
     * @param currentLatitude The current latitude
     * @param currentLongitude The current longitude
     * @param destinationLatitude The destination latitude
     * @param destinationLongitude The destination longitude
     * @return the json string
     */
    public String createFindGroupMessage(String name, Double currentLatitude, Double currentLongitude, Double destinationLatitude, Double destinationLongitude){
        JSONObject message = new JSONObject();
        try {
            if(name!=null){
                message.put("name",name);
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

    public JsonObject stringToJSONObject(String json){
        return new JsonParser().parse(json).getAsJsonObject();
    }

    /**
     * Converts json string representing a chat message to an actual message
     * @param messageInJson The message as a json string
     * @return The actual message
     */
    public Message convertJsonToChatMessage(String messageInJson) {
        JsonObject json = new JsonParser().parse(messageInJson).getAsJsonObject();
        return convertJsonToChatMessage(json);


    }

    /**
     * Converts json object representing a chat message to an actual message
     * @param jsonObjectMessage The message as a json object
     * @return The actual message
     */
    private Message convertJsonToChatMessage(JsonObject jsonObjectMessage) {
        String type = jsonObjectMessage.get("type").toString();
        String sender = jsonObjectMessage.get("sender").toString();

        Date time = handleTimeStamp(jsonObjectMessage.get("timestamp"));
        User user = new User(sender,null);


        if(type.equals("\"CHAT\"")){
            return new Message(jsonObjectMessage.get("content").getAsString(),user,time, MessageType.CHAT);
        }else if(type.equals("\"JOIN\"")){
            return new Message("JOIN", user, time, MessageType.JOIN);
        }else if(type.equals("\"LEAVE\"")){
            return new Message("LEAVE", user, time, MessageType.LEAVE);
        }else{
            //todo
            return new Message("", user, time, MessageType.CHAT);
        }
    }

    private Date handleTimeStamp(JsonElement timestamp) {
        if(timestamp==null){
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try {
            String test = timestamp.getAsString();
            return format.parse(test);


        } catch (ParseException e) {
            return null;
        }

    }

    /**
     * Converts json string representing a group to an actual group
     * @param jsonGroup The message as a json string
     * @return The group
     */
    public Group convertJsonToGroup(String jsonGroup){
        JsonObject jsonObject = new JsonParser().parse(jsonGroup).getAsJsonObject();
        JsonArray jsonUsers= jsonObject.getAsJsonArray("users");
        JsonArray jsonMessages = jsonObject.getAsJsonArray("messages");

        ArrayList<User> users = new ArrayList<>();

        if (jsonUsers != null) {
            int len = jsonUsers.size();
            for (int i=0;i<len;i++){
                users.add(createUserFromJsonUsers(jsonUsers.get(i).getAsJsonObject()));
            }
        }
        ArrayList<Message> messages = new ArrayList<>();
        if(jsonMessages !=null){
            int len = jsonMessages.size();
            for(int i=0;i<len;i++){
                messages.add(convertJsonToChatMessage(jsonMessages.get(i).getAsJsonObject()));
            }
        }

        String groupId= jsonObject.get("groupId").getAsString();

        return new Group(groupId,messages,users);
    }



    private User createUserFromJsonUsers(JsonObject userInJson) {
        String name = userInJson.get("name").getAsString();
        User newUser = new User(name,null);
        return newUser;
    }
}
