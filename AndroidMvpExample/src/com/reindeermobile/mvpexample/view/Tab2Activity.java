
package com.reindeermobile.mvpexample.view;

import com.reindeermobile.mvpexample.R;
import com.reindeermobile.mvpexample.mvp.MessageConstans;
import com.reindeermobile.mvpexample.mvp.Presenter;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Tab2Activity extends Activity implements Callback, OnClickListener {
    private TextView messageText;
    private Button refreshButton;

    @Override
    public boolean handleMessage(Message msg) {
        Log.d("view", "Tab2Activity.handleMessage: START");
        switch (msg.what) {
            case MessageConstans.M_UPDATE_TEXT:
                Log.d("view", "Tab2Activity.handleMessage: M_UPDATE_TEXT");
                if (msg.obj != null) {
                    this.messageText.setText((String) msg.obj);
                }
                break;
            case MessageConstans.M_LOCATION_UPDATE:
                Log.d("view", "Tab2Activity.handleMessage: M_LOCATION_UPDATE");
                if (msg.obj != null && msg.obj instanceof Location) {
                    Location location = (Location) msg.obj;
                    this.messageText.setText(location.getLongitude() + ", "
                            + location.getLatitude());
                }
                break;
            default:
                break;
        }
        Log.d("view", "Tab2Activity.handleMessage: END");
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            Log.d("view", "Tab1Activity.onClick: START");
            switch (v.getId()) {
                case R.id.buttonRefresh:
                    Presenter.getInst().sendModelMessage(MessageConstans.V_UPDATE_TEXT);
                    break;
                default:
                    break;
            }
            Log.d("view", "Tab1Activity.onClick: END");
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("view", "Tab2Activity.onCreate: START");
        setContentView(R.layout.tab2activity);

        Presenter.getInst().subscribe(this);

        this.messageText = (TextView) findViewById(R.id.textView1);
        this.refreshButton = (Button) findViewById(R.id.buttonRefresh);
        
        this.refreshButton.setOnClickListener(this);
        Log.d("view", "Tab2Activity.onCreate: END");
    }

}
