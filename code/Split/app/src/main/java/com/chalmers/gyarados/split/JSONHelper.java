package com.chalmers.gyarados.split;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;


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
        String type = json.get("type").toString();
        if(type.equals("\"CHAT\"")){
            return new Message(json.get("content").getAsString());
        }else if(type.equals("\"JOIN\"")){
            return new Message("JOIN");
        }else if(type.equals("\"LEAVE\"")){
            return new Message("LEAVE");
        }else{
            //todo
            return new Message("");
        }


    }
}
