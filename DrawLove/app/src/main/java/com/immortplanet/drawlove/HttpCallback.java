package com.immortplanet.drawlove;

import org.json.JSONObject;

/**
 * Created by tom on 4/30/17.
 */

public interface HttpCallback {
    public abstract void finished(JSONObject jsonObject);
}
