package com.shobhit.weatherforecast;

class ForcastObject {

   private String date,temperatue, pressure, humidity, weatherDescription, windSpeed, icon;

    ForcastObject(String date,String temperatue, String  pressure, String humidity, String weatherDescription,
                  String windSpeed, String icon){
        this.date = date;
        this.icon = icon;
        this.temperatue = temperatue;
        this.pressure = pressure;
        this.humidity = humidity;
        this.weatherDescription = weatherDescription;
        this.windSpeed = windSpeed;
    }

     String getIcon() {
        return icon;
    }

     void setIcon(String icon) {
        this.icon = icon;
    }

     String getDate() {
        return date;
    }

     String getHumidity() {
        return humidity;
    }

     String getPressure() {
        return pressure;
    }


     String getTemperatue() {
        return temperatue;
    }

     String getWeatherDescription() {
        return weatherDescription;
    }

     String getWindSpeed() {
        return windSpeed;
    }

}
