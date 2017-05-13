package com.immortplanet.drawlove.model;

import com.immortplanet.drawlove.util.AppDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tom on 5/3/17.
 */

public class Request extends DataModel{
    public String _id;
    public String sender;
    public String receiver;
    public String status;
    public String type;
    public String[] requestData;
    public String responseDate;

    public Request(JSONObject jsonObject){
        try{
            _id = jsonObject.getString("_id");
            sender = jsonObject.getString("sender");
            receiver = jsonObject.getString("receiver");
            status = jsonObject.getString("status");
            type = jsonObject.getString("type");
            JSONArray data = jsonObject.getJSONArray("requestData");
            requestData = new String[data.length()];
            for (int i=0; i<data.length(); i++){
                requestData[i] = data.getString(i);
            }
            responseDate = AppDateTime.parseJSDate(jsonObject.getString("responseDate"));
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

}
