package com.chalmers.gyarados.split;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;


public class JSONHelper {

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

    public Message convertJsonToChatMessage(String messageInJson) {
        JsonObject json = new JsonParser().parse(messageInJson).getAsJsonObject();
        return convertJsonToChatMessage(json);


    }

    private Message convertJsonToChatMessage(JsonObject jsonObjectMessage) {
        String type = jsonObjectMessage.get("type").toString();

        String time = handleTimeStamp(jsonObjectMessage.get("timestamp"));


        if(type.equals("\"CHAT\"")){
            return new Message(jsonObjectMessage.get("content").getAsString(),time);
        }else if(type.equals("\"JOIN\"")){
            return new Message("JOIN",time);
        }else if(type.equals("\"LEAVE\"")){
            return new Message("LEAVE",time);
        }else{
            //todo
            return new Message("",time);
        }
    }

    private String handleTimeStamp(JsonElement timestamp) {
        if(timestamp==null){
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String time;
        try {
            Date date = format.parse(timestamp.getAsString());
            format.applyPattern("hh:mm");
            time=format.format(date);

        } catch (ParseException e) {
            time="";
        }

        return time;
    }

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
        User newUser = new User();
        newUser.setName(name);
        return newUser;
    }
}
