
package com.reindeermobile.mvpexample.model;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.reindeermobile.mvpexample.entities.PlacesResponse;
import com.reindeermobile.mvpexample.entities.Result;
import com.reindeermobile.mvpexample.entities.ResultList;
import com.reindeermobile.mvpexample.mvp.IModel;
import com.reindeermobile.mvpexample.mvp.MessageConstans;
import com.reindeermobile.mvpexample.mvp.Presenter;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * https://maps.googleapis.com/maps/api/place/search/json?
 *      location=47.53287,21.628915
 *      &radius=250
 *      &sensor=false
 *      &key=AIzaSyBcwkVMGtWetX3zmq3OfWbsa0uDYr56bPM
 */
public class GeoModel implements IModel {
    private static final String LOG_TAG_MODEL = "model";
    public static final String HOST = "https://maps.googleapis.com";
    private static final String API_ACCESS_TOKEN = "AIzaSyBcwkVMGtWetX3zmq3OfWbsa0uDYr56bPM";
    private static final String PLACES_GET = "/maps/api/place/search/json";

    private Location location;
    // private PendingIntent pendingIntent;
    private LocationManager locationManager;
    private Context context;
    private Criteria criteria;
    private List<Result> placeResultList;

    private AsyncHttpClient httpClient;
    private AsyncHttpResponseHandler responseHandler;

    public class PlacesResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onStart() {
            super.onStart();
            Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.onStart: START");
        }

        @Override
        public void onFinish() {
            super.onFinish();
            Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.onFinish: START");
        }

        @Override
        protected void handleSuccessMessage(final String responseBody) {
            super.handleSuccessMessage(responseBody);
            Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.handleSuccessMessage: START");
            Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.handleSuccessMessage: " + responseBody);
        }

        @Override
        protected void handleFailureMessage(final Throwable e, final String responseBody) {
            super.handleFailureMessage(e, responseBody);
            Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.handleFailureMessage: START");
            Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.handleSuccessMessage: " + responseBody);
        }

        @Override
        protected void handleMessage(final Message msg) {
            super.handleMessage(msg);
            Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.handleMessage: START" + msg.what);
        }

        @Override
        public void onSuccess(final String content) {
            super.onSuccess(content);
            Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.onSuccess: START");
            final Gson gson = new Gson();
            final PlacesResponse placeResponse = gson.fromJson(content, PlacesResponse.class);
            final List<Result> results = placeResponse.results;

            if (results != null) {
                Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.onSuccess: SUCCESS");
                GeoModel.this.placeResultList = new
                        ArrayList<Result>(results);
                Presenter.getInst().sendViewMessage(MessageConstans.M_REFRESH_PLACES,
                        new ResultList(results));
            }
            Log.d(LOG_TAG_MODEL, "PlacesResponseHandler.onSuccess: END");
        }
    }

    @Override
    public boolean handleMessage(final Message msg) {
        Log.d(LOG_TAG_MODEL, "GeoModel.handleMessage: START");
        switch (msg.what) {
            case MessageConstans.M_LOCATION_CHANGED:
                Log.d(LOG_TAG_MODEL, "GeoModel.handleMessage: M_LOCATION_CHANGED");
                if (msg.obj != null && msg.obj instanceof Location) {
                    this.location = (Location) msg.obj;
                    Presenter.getInst().sendViewMessage(MessageConstans.M_LOCATION_UPDATE,
                            this.location);
                    Presenter.getInst().sendModelMessage(MessageConstans.M_UPDATE_PLACES);
                }
                break;
            case MessageConstans.M_UPDATE_PLACES:
                Log.d(LOG_TAG_MODEL, "GeoModel.handleMessage: M_UPDATE_PLACES");
                if (this.placeResultList == null) {
                    this.refreshPlaceList();
                }
                break;
            case MessageConstans.V_REFRESH_PLACES:
                Log.d(LOG_TAG_MODEL, "GeoModel.handleMessage: V_REFRESH_PLACES");
                this.refreshPlaceList();
                break;
            default:
                break;
        }
        Log.d(LOG_TAG_MODEL, "GeoModel.handleMessage: END");
        return false;
    }

    private void refreshPlaceList() {
        if (this.location != null) {
            final Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("location",
                    this.location.getLongitude() + "," + this.location.getLatitude());
            paramMap.put("radius", "250");
            paramMap.put("sensor", "false");
            paramMap.put("key", API_ACCESS_TOKEN);
            final RequestParams requestParams = new RequestParams(paramMap);
            this.httpClient.get(HOST + PLACES_GET, requestParams, this.responseHandler);
        }
    }

    @Override
    public void init(final Context context) {
        Log.d(LOG_TAG_MODEL, "GeoModel.init: START");
        this.context = context;

        this.criteria = new Criteria();
        this.criteria.setAccuracy(Criteria.ACCURACY_FINE);

        this.locationManager = (LocationManager) this.context
                .getSystemService(Context.LOCATION_SERVICE);
        this.locationManager.requestLocationUpdates(
                this.locationManager.getBestProvider(this.criteria, true), 5000, 0,
                this.locationListener);

        // Intent activeIntent = new Intent(context, LocationReceiver.class);
        // this.pendingIntent = PendingIntent.getBroadcast(context, 0,
        // activeIntent,
        // PendingIntent.FLAG_UPDATE_CURRENT);

        this.placeResultList = Collections.<Result> emptyList();
        this.httpClient = new AsyncHttpClient();
        this.responseHandler = new PlacesResponseHandler();
        Log.d(LOG_TAG_MODEL, "GeoModel.init: END");
    }

    protected LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        }

        @Override
        public void onProviderEnabled(final String provider) {
        }

        @Override
        public void onProviderDisabled(final String provider) {
        }

        @Override
        public void onLocationChanged(final Location location) {
            Log.d(LOG_TAG_MODEL, "LocationListener.onLocationChanged: START");
            if (location != null) {
                Presenter.getInst().sendModelMessage(MessageConstans.M_LOCATION_CHANGED, location);
            }
            Log.d(LOG_TAG_MODEL, "LocationListener.onLocationChanged: START");
        }
    };

}
