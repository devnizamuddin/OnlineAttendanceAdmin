package com.example.onlineattendanceadmin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckSystem {

    private Context context;

    public CheckSystem(Context context) {
        this.context = context;
    }

    public boolean havingInternetConnection() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                        == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
