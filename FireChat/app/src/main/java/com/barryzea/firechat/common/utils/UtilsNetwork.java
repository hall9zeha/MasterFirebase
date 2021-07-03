package com.barryzea.firechat.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.barryzea.firechat.ChatModule.view.ChatActivity;

public class UtilsNetwork {
    public static boolean isOnline(Context ctx) {
        ConnectivityManager connectivityManager =(ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null){
                return networkInfo.isConnected();
            }
        }
        return false;
    }
}
