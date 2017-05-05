package com.immortplanet.drawlove.model;

import com.immortplanet.drawlove.util.NodeDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    public String requestDate;

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
            requestDate = NodeDateTime.getDateFromID(_id);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

}
