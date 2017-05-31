package com.immortplanet.drawlove.model;

import com.immortplanet.drawlove.util.AppDateTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tom on 5/2/17.
 */

public class User extends DataModel {
    public String _id;
    public String chatID;
    public String joinedDate;
    public String email;
    public String profilePhoto;
    public ArrayList<User> friends;

    public ArrayList<Request> sentRequests;
    public ArrayList<Request> receivedRequests;
    public ArrayList<Group> groups;

    public User(JSONObject jsonObject){
        try{
            _id = jsonObject.getString("_id");
            profilePhoto = jsonObject.getString("profilePhoto");
            chatID = jsonObject.getString("chatID");
            email = jsonObject.getString("email");
            joinedDate = AppDateTime.getDateFromID(_id);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public boolean isFriend(String userID){
        for (int i=0; i<friends.size();i++){
            if (friends.get(i)._id.equals(userID)){
                return true;
            }
        }
        return false;
    }

    public boolean isBlocked(String userID, String type){
        for (int i=0; i<sentRequests.size(); i++){
            Request request = sentRequests.get(i);
            if (request.receiver.equals(userID) && request.type.equals(type) && request.status.equals("blocked")){
                return true;
            }
        }
        return false;
    }

    public boolean isBlocking(String userID, String type){
        for (int i = 0; i< receivedRequests.size(); i++){
            Request request = receivedRequests.get(i);
            if (request.sender.equals(userID) && request.type.equals(type) && request.status.equals("blocked")){
                return true;
            }
        }
        return false;
    }

    public boolean isPending(String userID, String type){
        for (int i=0; i<sentRequests.size(); i++){
            Request request = sentRequests.get(i);
            if (request.receiver.equals(userID) && request.type.equals(type) && request.status.equals("pending")){
                return true;
            }
        }
        return false;
    }

    public boolean isReceiving(String userID, String type){
        for (int i = 0; i< receivedRequests.size(); i++){
            Request request = receivedRequests.get(i);
            if (request.sender.equals(userID) && request.type.equals(type) && request.status.equals("pending")){
                return true;
            }
        }
        return false;
    }
}
