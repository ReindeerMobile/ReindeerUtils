
package com.reindeermobile.mvpexample.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {

    public LocationReceiver() {
        super();
        Log.d("model", "LocationReceiver: START");
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("model", "LocationReceiver.onReceive: START");
        Log.d("model", "LocationReceiver.onReceive: END");
    }

}
