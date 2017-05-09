package com.immortplanet.drawlove.model;

import java.util.HashMap;

/**
 * Created by tom on 5/2/17.
 */

public class DataSingleton {

    private HashMap<String, Object> data;

    private static DataSingleton dataSingleton;

    public static DataSingleton getDataSingleton(){
        if (dataSingleton == null){
            dataSingleton = new DataSingleton();
        }
        return dataSingleton;
    }

    public Object get(String key){
        return data.get(key);
    }

    public void put(String key, Object obj){
        data.put(key, obj);
    }

    public DataSingleton(){
        data = new HashMap<>();
    }
}
