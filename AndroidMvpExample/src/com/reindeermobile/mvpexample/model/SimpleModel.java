
package com.reindeermobile.mvpexample.model;

import com.reindeermobile.mvpexample.mvp.IModel;
import com.reindeermobile.mvpexample.mvp.MessageConstans;
import com.reindeermobile.mvpexample.mvp.Presenter;

import android.content.Context;
import android.os.Message;
import android.util.Log;

public class SimpleModel implements IModel {
    private String text;

    @Override
    public boolean handleMessage(final Message msg) {
        Log.d("model", "SimpleModel.handleMessage: START");
        switch (msg.what) {
            case MessageConstans.V_UPDATE_TEXT:
                Log.d("model", "SimpleModel.handleMessage: V_UPDATE_TEXT");
                if (msg.obj != null && msg.obj instanceof String) {
                    this.text = (String) msg.obj;
                }
                Presenter.getInst().sendViewMessage(MessageConstans.M_UPDATE_TEXT, this.text);
                break;
            default:
                break;
        }
        Log.d("model", "SimpleModel.handleMessage: END");
        return false;
    }

    @Override
    public void init(final Context context) {
        this.text = "";
    }

}
