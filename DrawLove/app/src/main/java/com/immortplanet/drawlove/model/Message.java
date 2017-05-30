package com.immortplanet.drawlove.model;

import com.immortplanet.drawlove.util.AppDateTime;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tom on 5/5/17.
 */

public class Message extends DataModel {

    public final int SENDING = 0;
    public final int SENT = 1;

    public String _id;
    public int contentType;
    public String contentMetaData;
    public String content;
    public String sender;
    public String group;
    public String sentDate;

    public int status;

    public Message(){
        status = SENDING;
    }

    public Message(JSONObject jsonObject){
        try{
            _id = jsonObject.getString("_id");
            contentType = jsonObject.getInt("contentType");
            contentMetaData = jsonObject.isNull("contentMetaData")? null : jsonObject.getString("contentMetaData");
            content = jsonObject.getString("content");
            sender = jsonObject.getString("sender");
            group = jsonObject.getString("group");
            sentDate = AppDateTime.getDateFromID(_id);
            status = SENT;
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
}
