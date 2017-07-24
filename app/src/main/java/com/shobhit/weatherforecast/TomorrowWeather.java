package com.shobhit.weatherforecast;

import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TomorrowWeather extends Fragment {

    TextView tvCityName, tvPressure, tvHumidity, tvTemperatureMin, tvTemperatureMax, tvWeatherDescription,tvCloud,
            tvWindSpeed, tvWindDegree,tvLastUpdate, tvTemperatureDay, tvRain, tvSnow;
    ImageView ivWeatherIcon;
    String Sspeed,Stemp_max,Stemp_min,Scityname,Scondition,StemperatureDay,Shumidity,Spressure, Sdate, SCloud,
            SiconText,SwindDegree,SRain,SSnow,tomorrowAsString;
    DateFormat dateFormat;
    CheckNetworkConnectivity checkNetworkConnectivity;
    ProgressBar pbTomorrow;
    //weather api url
    private String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&units=metric";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tomorrow_weather, container, false);

        tvCityName = (TextView) view.findViewById(R.id.textViewTomorrowCityName);
        tvTemperatureDay = (TextView) view.findViewById(R.id.textViewTomorrowTemperature);
        tvPressure = (TextView) view.findViewById(R.id.textViewTomorrowPresure);
        tvHumidity = (TextView) view.findViewById(R.id.textViewTomorrowHumidity);
        tvTemperatureMin = (TextView) view.findViewById(R.id.textViewTomorrowTemperatureMin);
        tvTemperatureMax = (TextView) view.findViewById(R.id.textViewTomorrowTemperatureMax);
        tvWeatherDescription = (TextView) view.findViewById(R.id.textViewTomorrowWearherDescription);
        tvWindSpeed = (TextView) view.findViewById(R.id.textViewTomorrowWindSpeed);
        tvWindDegree = (TextView) view.findViewById(R.id.textViewTomorrowWindDegree);
        tvLastUpdate = (TextView) view.findViewById(R.id.textViewTomorrowLastUpdate);
        tvCloud = (TextView) view.findViewById(R.id.textViewTomorrowCloud);
        tvRain = (TextView) view.findViewById(R.id.textViewTomorrowRain);
        tvSnow = (TextView) view.findViewById(R.id.textViewTomorrowSnow);
        ivWeatherIcon = (ImageView) view.findViewById(R.id.imageViewTomorrowWeatherIcon);
        pbTomorrow = (ProgressBar) view.findViewById(R.id.progressBartomorrow);

        //getting the value of latitude, longitude, city from
        //the sharedPreference which stores its value
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
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

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        tomorrowAsString = dateFormat.format(tomorrow);

        checkNetworkConnectivity = new CheckNetworkConnectivity();

        //if there is internet then make a call to api
        if(checkNetworkConnectivity.isNetConnected(getContext())) {
            APICalling apiCalling = new APICalling(getContext(), new APIDelecate() {
                @Override
                public void processFinish(JSONObject jsonObject) {
                    pbTomorrow.setVisibility(View.GONE);
                    jsonToString(jsonObject);
                }
            });
            apiCalling.execute(WEATHER_URL);
        }else {
            alertDialog(getContext());
        }
        return view;
    }

    //Handle json string and getting values from it and
    //add it to the ArrayList
    private void jsonToString(JSONObject jsonObject) {
        JSONArray weatherArray;
        try {
            if(jsonObject!=null) {
                weatherArray = jsonObject.getJSONArray("list");
                JSONObject JOCity = jsonObject.getJSONObject("city");
                for(int i = 0 ; i<weatherArray.length(); i++){

                    JSONObject jsonObject1 = weatherArray.getJSONObject(i);
                    Sdate = dateFormat.format(new Date(jsonObject1.getLong("dt") * 1000));

                    System.out.println(Sdate+" date "+ tomorrowAsString);

                    if(Sdate.equals(tomorrowAsString)){
                        Shumidity = jsonObject1.getString("humidity") + "%";
                        Spressure = jsonObject1.getString("pressure") + " hPa";
                        JSONObject JOTemp = jsonObject1.getJSONObject("temp");
                        double temp_max = JOTemp.getDouble("max");
                        double temp_min = JOTemp.getDouble("min");
                        double temp = JOTemp.getDouble("day");
                        if(temp>100){
                            temp = temp - 273.15;
                            temp_max = temp_max - 273.15;
                            temp_min = temp_min - 273.15;
                        }
                        Stemp_max = String.format("%.1f", temp_max) + "°C";
                        Stemp_min = String.format("%.1f", temp_min) + "°C";
                        StemperatureDay = String.format("%.1f", temp) + "°C";
                        JSONObject JOweather = jsonObject1.getJSONArray("weather").getJSONObject(0);
                        SiconText = JOweather.getString("icon");
                        Scondition = JOweather.getString("description").toUpperCase(Locale.US);
                        Sspeed = jsonObject1.getString("speed") + "m/sec";
                        Scityname = JOCity.getString("name") + ", " + JOCity.getString("country");
                        SwindDegree = jsonObject1.getString("deg");
                        SCloud = jsonObject1.getString("clouds");
                        try {
                            SRain = jsonObject1.getString("rain")+ " mm";
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        try {
                            SSnow = jsonObject1.getString("snow");
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setValueToView();
    }

    //set value from json to the view
    private void setValueToView() {
        tvCityName.setText(Scityname);
        tvTemperatureDay.setText(StemperatureDay);
        tvWeatherDescription.setText(Scondition);
        tvWindSpeed.setText(Sspeed);
        tvTemperatureMax.setText(getString(R.string.s_temp_max)+Stemp_max);
        tvTemperatureMin.setText(getString(R.string.s_temp_min)+Stemp_min);
        tvPressure.setText(Spressure);
        tvHumidity.setText(Shumidity);
        tvLastUpdate.setText(Sdate);
        tvCloud.setText(getString(R.string.s_cloud)+SCloud);
        if(SRain!=null)
            tvRain.setText(getString(R.string.s_rain)+SRain);
        else
            tvRain.setVisibility(View.GONE);
        if(SSnow!=null)
            tvSnow.setText(getString(R.string.s_snow)+SSnow);
        else
            tvSnow.setVisibility(View.GONE);
        tvWindDegree.setText(SwindDegree);
        Picasso.with(getContext())
                .load("http://openweathermap.org/img/w/"+SiconText+".png")
                .into(ivWeatherIcon);

    }
    //show the alert dialog if there is no internet connection
    public void alertDialog(final Context mContext){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mContext);
        alertDialog.setTitle("Internet Error");
        alertDialog.setMessage("No Internet found or Internet Connectivity failed. Please try again later");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               getActivity().finish();
            }
        });
        alertDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}

