
package com.reindeermobile.mvpexample.view;

import com.reindeermobile.mvpexample.R;
import com.reindeermobile.mvpexample.entities.Result;
import com.reindeermobile.mvpexample.entities.ResultList;
import com.reindeermobile.mvpexample.mvp.MessageConstans;
import com.reindeermobile.mvpexample.mvp.Presenter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Tab3Activity extends Activity implements Callback, OnClickListener {
    private ListView placesListView;
    private Button refreshButton;
    private List<Result> placeResultList;

    @Override
    public boolean handleMessage(Message msg) {
        Log.d("view", "Tab3Activity.handleMessage: START");
        switch (msg.what) {
            case MessageConstans.M_REFRESH_PLACES:
                Log.d("view", "Tab3Activity.handleMessage: M_REFRESH_PLACES");
                if (msg.obj != null && msg.obj instanceof ResultList) {
                    this.placeResultList = ((ResultList) msg.obj).getResultList();
                    updateList();
                }
                break;
            default:
                break;
        }
        Log.d("view", "Tab3Activity.handleMessage: END");
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.buttonRefresh:
                    Presenter.getInst().sendModelMessage(MessageConstans.V_REFRESH_PLACES);
                    break;
                default:
                    Log.w("view", "Tab3Activity.onClick: unknown view");
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab3);

        Presenter.getInst().subscribe(this);

        this.placesListView = (ListView) findViewById(R.id.listviewNearestPlaces);
        this.refreshButton = (Button) findViewById(R.id.buttonRefresh);

        this.refreshButton.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.placeResultList == null) {
            this.placeResultList = new ArrayList<Result>();
        }
        Presenter.getInst().sendModelMessage(MessageConstans.V_REFRESH_PLACES);
    }

    private void updateList() {
        Log.d("view", this.placeResultList.toString());
        PlacesListAdapter placesListAdapter = new PlacesListAdapter(this,
                R.id.listviewNearestPlaces, placeResultList);
        this.placesListView.setAdapter(placesListAdapter);
    }
}
