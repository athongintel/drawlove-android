package com.immortplanet.drawlove.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tom on 5/3/17.
 */

public class Group extends DataModel {
    public String _id;
    public String name;
    public ArrayList<String> members;
    public ArrayList<Message> messages;

    public Group(JSONObject jsonObject){
        try {
            _id = jsonObject.getString("_id");
            name = jsonObject.getString("name");
            JSONArray members = (JSONArray)jsonObject.getJSONArray("members");
            this.members = new ArrayList<>();
            for (int j=0; j<members.length(); j++){
                this.members.add(members.getString(j));
            }
            messages = new ArrayList<>();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
