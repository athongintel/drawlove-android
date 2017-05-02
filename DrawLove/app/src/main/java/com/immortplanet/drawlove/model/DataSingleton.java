package com.immortplanet.drawlove.model;

import java.util.HashMap;

/**
 * Created by tom on 5/2/17.
 */

public class DataSingleton {

    public HashMap<String, DataModel> data;

    private static DataSingleton dataSingleton;

    public static DataSingleton getDataSingleton(){
        if (dataSingleton == null){
            dataSingleton = new DataSingleton();
        }
        return dataSingleton;
    }


    public DataSingleton(){
        data = new HashMap<>();
    }
}
