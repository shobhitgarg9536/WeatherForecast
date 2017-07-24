package com.shobhit.weatherforecast;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ForecastWeekly extends AppCompatActivity {

    //weather api url
    private String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&units=metric";
    RecyclerView recyclerView;
    Toolbar tbForecast;
    CheckNetworkConnectivity checkNetworkConnectivity;
    ProgressBar pbForecast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forcast_weekly);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewForecast);
        tbForecast = (Toolbar) findViewById(R.id.tbForcastWeekly);
        pbForecast = (ProgressBar) findViewById(R.id.progressBarforecast);
        setSupportActionBar(tbForecast);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getting the value of latitude, longitude, city from
        //the sharedPreference which stores its value
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        String latitude = sharedPreferences.getString("latitude", "");
        String longitude = sharedPreferences.getString("longitude", "");
        String city = sharedPreferences.getString("city", "");

        //if the value of the city is empty then
        //show the current location weather
        if(city.isEmpty()) {
            WEATHER_URL = String.format(WEATHER_URL,latitude,longitude);
        }
        //else show the weather report of the city
        else
            WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q="+city;

        checkNetworkConnectivity = new CheckNetworkConnectivity();

        //if there is internet then make a call to api
        if(checkNetworkConnectivity.isNetConnected(ForecastWeekly.this)) {
            APICalling apiCalling = new APICalling(this, new APIDelecate() {
                @Override
                public void processFinish(JSONObject jsonObject) {
                    pbForecast.setVisibility(View.GONE);
                    jsonToString(jsonObject);
                }
            });
            apiCalling.execute(String.format(WEATHER_URL, latitude, longitude));
        }else {
           alertDialog(ForecastWeekly.this);
        }
    }

    //Handle json string and getting values from it and
    //add it to the ArrayList
    private void jsonToString(JSONObject jsonObject) {
        ArrayList<ForcastObject> weeklyForecastArrayList = new ArrayList<>();
        JSONArray weatherArray;//as the json is in array form
        try {
            if(jsonObject!=null) {
                weatherArray = jsonObject.getJSONArray("list");
                JSONObject JOCity = jsonObject.getJSONObject("city");
                DateFormat df = DateFormat.getDateTimeInstance();

               String Scityname = JOCity.getString("name") + ", " + JOCity.getString("country");
                tbForecast.setTitle(Scityname);

                for(int i = 0 ; i<weatherArray.length(); i++){

                    JSONObject jsonObject1 = weatherArray.getJSONObject(i);
                   String Sdate = df.format(new Date(jsonObject1.getLong("dt") * 1000));
                    Sdate = Sdate.substring(0,Sdate.length()-8);
                       String Shumidity = jsonObject1.getString("humidity") + "%";
                       String Spressure = jsonObject1.getString("pressure") + " hPa";
                        JSONObject JOTemp = jsonObject1.getJSONObject("temp");
                       double temperatureDay = JOTemp.getDouble("day");
                    if(temperatureDay>100)
                        temperatureDay = temperatureDay-273.15;
                       String StemperatureDay = String.format("%.1f", temperatureDay);
                        JSONObject JOweather = jsonObject1.getJSONArray("weather").getJSONObject(0);
                       String SiconText = JOweather.getString("icon");
                       String Scondition = JOweather.getString("description").toUpperCase(Locale.US);
                       String Sspeed = jsonObject1.getString("speed") + "m/sec";
                        ForcastObject forcastObject = new ForcastObject(Sdate,StemperatureDay,Spressure,Shumidity
                        ,Scondition,Sspeed,"http://openweathermap.org/img/w/"+SiconText+".png");
                    weeklyForecastArrayList.add(forcastObject);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setView(weeklyForecastArrayList);
    }

    //Declare the adapter class and set the RecyclerView with
    //the details of the weather forecast which contains in the ArrayList
    private void setView(ArrayList<ForcastObject> weeklyForecastArrayList) {
        ForecastWeeklyAdapter forecastWeeklyAdapter = new ForecastWeeklyAdapter(weeklyForecastArrayList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(forecastWeeklyAdapter);
    }

    //show the alert dialog if there is no internet connection
    public void alertDialog(final Context mContext){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mContext);
        alertDialog.setTitle("Internet Error");
        alertDialog.setMessage("No Internet found or Internet Connectivity failed. Please try again later");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
