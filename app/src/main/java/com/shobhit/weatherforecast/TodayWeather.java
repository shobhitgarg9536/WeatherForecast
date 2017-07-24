package com.shobhit.weatherforecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class TodayWeather extends Fragment {

    TextView tvCityName, tvTemperature, tvPressure, tvHumidity, tvTemperatureMin, tvTemperatureMax, tvWeatherDescription,
              tvWindSpeed, tvWindDegree,tvLastUpdate;
    ImageView ivWeatherIcon;
    String Sspeed,Stemp_max,Stemp_min,Scityname,Scondition,Stemperature,Shumidity,Spressure,SlastUpdate,
           SiconText,SwindDegree;
    CheckNetworkConnectivity checkNetworkConnectivity;
    ProgressBar pbToday;
    //weather api url
    private String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_weather, container, false);

        tvCityName = (TextView) view.findViewById(R.id.textViewCityName);
        tvTemperature = (TextView) view.findViewById(R.id.textViewTemperature);
        tvPressure = (TextView) view.findViewById(R.id.textViewPresure);
        tvHumidity = (TextView) view.findViewById(R.id.textViewHumidity);
        tvTemperatureMin = (TextView) view.findViewById(R.id.textViewTemperatureMin);
        tvTemperatureMax = (TextView) view.findViewById(R.id.textViewTemperatureMax);
        tvWeatherDescription = (TextView) view.findViewById(R.id.textViewWearherDescription);
        tvWindSpeed = (TextView) view.findViewById(R.id.textViewWindSpeed);
        tvWindDegree = (TextView) view.findViewById(R.id.textViewWindDegree);
        tvLastUpdate = (TextView) view.findViewById(R.id.textViewLastUpdate);
        ivWeatherIcon = (ImageView) view.findViewById(R.id.imageViewWeatherIcon);
        pbToday = (ProgressBar) view.findViewById(R.id.progressBartoday);

        //getting the value of latitude, longitude, city from
        //the sharedPreference which stores its value
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        String latitude = sharedPreferences.getString("latitude", "");
        String longitude = sharedPreferences.getString("longitude", "");
        String city = sharedPreferences.getString("city", "");

        //if the value of the city is empty then
        //show the current location weather
        if(city.isEmpty() || city.equals("")) {
            WEATHER_URL = String.format(WEATHER_URL,latitude,longitude);
        }
        //else show the weather report of the city
        else
            WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q="+city;

        checkNetworkConnectivity = new CheckNetworkConnectivity();

        //if there is internet then make a call to api
        if(checkNetworkConnectivity.isNetConnected(getContext())) {
            APICalling apiCalling = new APICalling(getContext(), new APIDelecate() {
                @Override
                public void processFinish(JSONObject jsonObject) {
                    pbToday.setVisibility(View.GONE);
                    jsonToString(jsonObject);
                }
            });
            apiCalling.execute(WEATHER_URL);
        }
        return view;
    }

    //Handle json string and getting values from it and
    //add it to the ArrayList
    private void jsonToString(JSONObject jsonObject) {
        JSONObject weatherArray = null;//as the jason is in array form
        try {
            if(jsonObject!=null) {
                weatherArray = jsonObject.getJSONArray("weather").getJSONObject(0);
                JSONObject main = jsonObject.getJSONObject("main_menu");
                DateFormat df = DateFormat.getDateTimeInstance();
                JSONObject wind = jsonObject.getJSONObject("wind");
                double temp = main.getDouble("temp");
                double temp_min = main.getDouble("temp_min");
                double temp_max = main.getDouble("temp_max");
                if(temp>100){
                    temp = temp - 273.15;
                    temp_max = temp_max - 273.15;
                    temp_min = temp_min - 273.15;
                }
                Stemp_max = String.format("%.1f", temp_max) + "°C";
                Stemp_min = String.format("%.1f", temp_min) + "°C";
                Stemperature = String.format("%.1f", temp) + "°C";//to covert to 1 decimal place
                Sspeed = wind.getString("speed") + " Km/hr";
                SwindDegree = wind.getString("deg")+ "°";
                Scondition = weatherArray.getString("description").toUpperCase(Locale.US);
                Shumidity = main.getString("humidity") + "%";
                Spressure = main.getString("pressure") + " hPa";
                SlastUpdate = df.format(new Date(jsonObject.getLong("dt") * 1000));
                SiconText = weatherArray.getString("icon");
                Scityname = jsonObject.getString("name") + ", " + jsonObject.getJSONObject("sys").getString("country");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setValueToView();
    }

    //set value from json to the view
    private void setValueToView() {
        tvCityName.setText(Scityname);
        tvTemperature.setText(Stemperature);
        tvWeatherDescription.setText(Scondition);
        tvWindSpeed.setText(Sspeed);
        tvTemperatureMax.setText(getString(R.string.s_temp_max)+Stemp_max);
        tvTemperatureMin.setText(getString(R.string.s_temp_min)+Stemp_min);
        tvPressure.setText(Spressure);
        tvHumidity.setText(Shumidity);
        tvLastUpdate.setText(getString(R.string.s_last_update)+SlastUpdate);
        tvWindDegree.setText(SwindDegree);
        Picasso.with(getContext())
                .load("http://openweathermap.org/img/w/"+SiconText+".png")
                .into(ivWeatherIcon);

    }

}
