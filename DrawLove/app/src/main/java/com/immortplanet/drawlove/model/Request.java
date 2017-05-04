package com.immortplanet.drawlove.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tom on 5/3/17.
 */

public class Request extends DataModel{
    public String sender;
    public String receiver;
    public String status;
    public String type;

    public Request(JSONObject jsonObject){
        try{
            sender = jsonObject.getString("sender");
            receiver = jsonObject.getString("receiver");
            status = jsonObject.getString("status");
            type = jsonObject.getString("type");
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

}
