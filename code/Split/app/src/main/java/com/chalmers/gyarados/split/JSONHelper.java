package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.Group;
import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.User;
import com.google.gson.Gson;
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
    /**
     * Creates a json string that represents a chat message
     * @param user the user of the message
     * @param content The content of the message
     * @param type The type of message
     * @return the json string
     */
    public String createChatMessage(User user, String content, String type){

        JSONObject message = new JSONObject();
        try {
            if(user!=null){

                message.put("sender",convertUserToJsonObject(user));
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
        JsonObject sender = jsonObjectMessage.get("sender").getAsJsonObject();


        Date time = handleTimeStamp(jsonObjectMessage.get("timestamp"));
        String name = sender.get("name").getAsString();
        String id = sender.get("userID").getAsString();
        User user = new User(name,id,null);


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

    public String convertChatMessageToJSon(Message message) {
        JSONObject json = new JSONObject();
        try {
            json.put("sender",convertUserToJsonObject(message.getSender()));
            json.put("content",message.getMessage());
            json.put("type",message.getType().toString());
        } catch (JSONException e) {
            return null;
        }
        return json.toString();
    }

    private JSONObject convertUserToJsonObject(User sender) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name",sender.getName());
            jsonObject.put("userID",sender.getUserId());
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }
}
