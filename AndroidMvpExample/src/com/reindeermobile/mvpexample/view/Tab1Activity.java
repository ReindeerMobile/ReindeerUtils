
package com.reindeermobile.mvpexample.view;

import com.reindeermobile.mvpexample.R;
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
import android.widget.TextView;

public class Tab1Activity extends Activity implements Callback, OnClickListener {
    private TextView textView;
    private Button button;

    @Override
    public boolean handleMessage(final Message msg) {
        Log.d("view", "Tab1Activity.handleMessage: START");
        switch (msg.what) {
            case MessageConstans.M_UPDATE_TEXT:
                Log.d("view", "Tab1Activity.handleMessage: M_UPDATE_TEXT");
                if (msg.obj != null) {
                    this.textView.setText((String) msg.obj);
                }
                break;
            default:
                break;
        }
        Log.d("view", "Tab1Activity.handleMessage: END");
        return false;
    }

    @Override
    public void onClick(final View v) {
        if (v != null) {
            Log.d("view", "Tab1Activity.onClick: START");
            switch (v.getId()) {
                case R.id.button1:
                    Presenter.getInst().sendModelMessage(MessageConstans.V_UPDATE_TEXT,
                            this.textView.getText().toString());
                    break;
                default:
                    break;
            }
            Log.d("view", "Tab1Activity.onClick: END");
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("view", "Tab1Activity.onCreate: START");
        this.setContentView(R.layout.activity_tab1);

        Presenter.getInst().subscribe(this);

        this.textView = (TextView) this.findViewById(R.id.editText1);

        this.button = (Button) this.findViewById(R.id.button1);
        this.button.setOnClickListener(this);
        Log.d("view", "Tab1Activity.onCreate: END");
    }

}
