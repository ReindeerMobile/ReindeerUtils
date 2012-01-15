
package com.reindeermobile.mvpexample.mvp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Presenter implements Callback {
    private List<Handler> modelHandlerList;
    private List<Handler> viewHandlerList;

    private HashMap<Callback, Handler> handlerMap;
    private Context context;

    private Handler handler;
    private HandlerThread handlerThread;

    private static volatile Presenter INSTANCE;

    public static final void initInstance(Context context, List<IModel> modelList) {
        INSTANCE = new Presenter(context, modelList);
    }

    public static final Presenter getInst() {
        return INSTANCE;
    }

    private Presenter(Context context, List<IModel> modelList) {
        this.context = context;

        this.handlerMap = new HashMap<Callback, Handler>();
        this.modelHandlerList = new ArrayList<Handler>(modelList.size());
        this.viewHandlerList = new ArrayList<Handler>();

        for (int i = 0; i < modelList.size(); i++) {
            HandlerThread handlerThread = new HandlerThread(modelList.get(i).getClass()
                    .getName());
            handlerThread.start();
            this.modelHandlerList
                    .add(new Handler(handlerThread.getLooper(), modelList.get(i)));
        }

        // TODO ezt a rész még nem vágom
        this.handlerThread = new HandlerThread(getClass().getCanonicalName());
        this.handlerThread.start();
        this.handler = new Handler(this.handlerThread.getLooper(), this);
    }

    public final void subscribe(Callback viewComponentCallback) {
        Handler handler = new Handler(viewComponentCallback);
        this.handlerMap.put(viewComponentCallback, handler);
        this.viewHandlerList.add(handler);
    }

    public final void unsubscribe(Callback viewComponentallback) {
        List<Handler> newList = new ArrayList<Handler>(this.viewHandlerList);
        newList.remove(this.handlerMap.get(viewComponentallback));
        this.handlerMap.remove(viewComponentallback);
        this.viewHandlerList = newList;
    }

    public final void dispose() {
        for (Handler handler : this.modelHandlerList) {
            handler.getLooper().quit();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    public Context getApplicationContext() {
        return this.context;
    }

    public final void sendViewMessage(int what, int arg1, int arg2, Object obj, Bundle bundle) {
        List<Handler> outBoxList = this.viewHandlerList;
        for (Handler handler : outBoxList) {
            sendMessageToTarget(handler, what, arg1, arg2, obj, bundle);
        }
    }

    public void sendViewMessage(int what) {
        this.sendViewMessage(what, 0, 0, null, null);
    }

    public void sendViewMessage(int what, Object obj) {
        this.sendViewMessage(what, 0, 0, obj, null);
    }

    public void sendModelMessage(int what) {
        this.sendModelMessage(what, 0, 0, null, null);
    }

    public void sendModelMessage(int what, Object obj) {
        this.sendModelMessage(what, 0, 0, obj, null);
    }

    public final void sendModelMessage(int what, int arg1, int arg2, Object obj, Bundle bundle) {
        Log.d("presenter", "Presenter.sendModelMessage: START");
        for (Handler handler : this.modelHandlerList) {
            sendMessageToTarget(handler, what, arg1, arg2, obj, bundle);
        }
        Message.obtain(handler, what, arg1, arg2, obj).sendToTarget();
        Log.d("presenter", "Presenter.sendModelMessage: END");
    }

    private void sendMessageToTarget(Handler handler, int what, int arg1, int arg2, Object obj,
            Bundle bundle) {
        Message message = Message.obtain(handler, what, arg1, arg2, obj);
        message.setData(bundle);
        message.sendToTarget();
    }

}
