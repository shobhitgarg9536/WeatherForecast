package com.shobhit.weatherforecast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//this class is called when we used to check internet
//connection in the app
 class CheckNetworkConnectivity {

    static boolean isNetConnected(Context mContext) {
        NetworkInfo netInfo = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}