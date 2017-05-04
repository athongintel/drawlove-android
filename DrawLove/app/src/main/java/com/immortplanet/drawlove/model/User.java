package com.immortplanet.drawlove.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tom on 5/2/17.
 */

public class User extends DataModel {
    public String _id;
    public String chatID;
    public String joinedDate;
    public String email;
    public ArrayList<String> friends;
    public ArrayList<Request> requests;
    public ArrayList<Request> friendRequests;

    public User(JSONObject jsonObject){
        try{
            _id = jsonObject.getString("_id");
            chatID = jsonObject.getString("chatID");
            email = jsonObject.getString("email");
            Calendar calendar = Calendar.getInstance();
            long milliSeconds = Long.parseLong(jsonObject.getString("_id").substring(0, 8), 16)*1000;
            calendar.setTimeInMillis(milliSeconds);
            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            joinedDate = dateFormatter.format(calendar.getTime());
            friends = new ArrayList<>();
            JSONArray arFriends = (JSONArray)jsonObject.getJSONArray("friends");
            for (int i=0; i<arFriends.length(); i++){
                friends.add(arFriends.getString(i));
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public boolean isFriend(String userID){
        return friends.contains(userID);
    }

    public boolean isBlocked(String userID, String type){
        boolean isBlocked = false;
        for (int i=0; i<requests.size(); i++){
            Request request = requests.get(i);
            if (request.receiver.equals(userID) && request.type.equals(type) && request.status.equals("blocked")){
                isBlocked = true;
                break;
            }
        }
        return isBlocked;
    }

    public boolean isBlocking(String userID, String type){
        boolean isBlocking = false;
        for (int i=0; i<friendRequests.size(); i++){
            Request request = friendRequests.get(i);
            if (request.sender.equals(userID) && request.type.equals(type) && request.status.equals("blocked")){
                isBlocking = true;
                break;
            }
        }
        return isBlocking;
    }

    public boolean isPending(String userID, String type){
        boolean isPending = false;
        for (int i=0; i<requests.size(); i++){
            Request request = requests.get(i);
            if (request.receiver.equals(userID) && request.type.equals(type) && request.status.equals("pending")){
                isPending = true;
                break;
            }
        }
        return isPending;
    }

    public boolean isReceiving(String userID, String type){
        boolean isReceiving = false;
        for (int i=0; i<friendRequests.size(); i++){
            Request request = friendRequests.get(i);
            if (request.sender.equals(userID) && request.type.equals(type) && request.status.equals("pending")){
                isReceiving = true;
                break;
            }
        }
        return isReceiving;
    }
}
