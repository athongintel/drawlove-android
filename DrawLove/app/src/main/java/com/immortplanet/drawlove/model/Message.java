package com.immortplanet.drawlove.model;

import com.immortplanet.drawlove.util.NodeDateTime;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tom on 5/5/17.
 */

public class Message extends DataModel {

    String _id;
    int contentType;
    String contentMetaData;
    String content;
    String sender;
    String group;
    String sentDate;

    public Message(JSONObject jsonObject){
        try{
            _id = jsonObject.getString("_id");
            contentType = jsonObject.getInt("contentType");
            contentMetaData = jsonObject.getString("contentMetaData");
            content = jsonObject.getString("content");
            sender = jsonObject.getString("sender");
            group = jsonObject.getString("group");
            sentDate = NodeDateTime.getDateFromID("_id");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
}
