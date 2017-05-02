package com.immortplanet.drawlove.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tom on 5/3/17.
 */

public class Group extends DataModel {
    public long id;
    public String _id;
    public String name;
    public String[] members;

    public Group(JSONObject jsonObject){
        try {
            _id = jsonObject.getString("_id");
            name = jsonObject.getString("name");
            JSONArray members = (JSONArray)jsonObject.getJSONArray("members");
            this.members = new String[members.length()];
            for (int j=0; j<members.length(); j++){
                this.members[j] = members.getString(j);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
