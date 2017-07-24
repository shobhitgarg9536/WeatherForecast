package com.shobhit.weatherforecast;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class APICalling extends AsyncTask<String , JSONObject ,JSONObject> {

    //initialising
    private Context mContext;
    private APIDelecate apiDelecate = null;

    //defining constructor
     APICalling(Context context ,APIDelecate Response ){
        apiDelecate = Response;
        mContext = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        //getting API URL
        String WEATHER_URL = params[0];

        try
        {
            URL url = new URL(WEATHER_URL+"&APPID="+mContext.getString(R.string.API_KEY));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //to get input from url
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String readData="";
            //reading json data recieve from the url
            while((readData=reader.readLine())!=null)
                json.append(readData).append("\n");
            reader.close();
            //converting string to the json object
            JSONObject data = new JSONObject(json.toString());
            if(data.getInt("cod") == 200)//an internal parameter for this weather json
            {
                return data;
            }
        }
         catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject s) {
            apiDelecate.processFinish(s);
    }
}