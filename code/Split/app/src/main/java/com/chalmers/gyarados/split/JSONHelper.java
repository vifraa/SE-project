package com.chalmers.gyarados.split;

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
}
