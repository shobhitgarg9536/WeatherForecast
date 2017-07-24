package com.shobhit.weatherforecast;

import org.json.JSONObject;

//this interface helps to transfer the json object
//from the APICalling class to the other class which call
//that APICalling class by its object and implements this interface
interface APIDelecate {
    void processFinish(JSONObject jsonObject);
}
