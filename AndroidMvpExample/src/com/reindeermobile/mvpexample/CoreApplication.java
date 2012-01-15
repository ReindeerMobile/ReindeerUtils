
package com.reindeermobile.mvpexample;

import com.reindeermobile.mvpexample.model.GeoModel;
import com.reindeermobile.mvpexample.model.SimpleModel;
import com.reindeermobile.mvpexample.mvp.IModel;
import com.reindeermobile.mvpexample.mvp.Presenter;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class CoreApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        List<IModel> modelList = new ArrayList<IModel>();

        // Modellek regisztrálása
        modelList.add(new SimpleModel());
        modelList.add(new GeoModel());

        // A modellek inicializálása
        for (IModel model : modelList) {
            model.init(this);
        }

        // Presenter inicializálása
        Presenter.initInstance(getApplicationContext(), modelList);

        // Location frissítés engedélyezése
        // Presenter.getInst().sendModelMessage(MessageConstans.V_LOCATION_ENABLE);
    }

}
