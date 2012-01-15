
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
    private final List<Handler> modelHandlerList;
    private List<Handler> viewHandlerList;

    private final HashMap<Callback, Handler> handlerMap;
    private final Context context;

    private final Handler handler;
    private final HandlerThread handlerThread;

    private static volatile Presenter INSTANCE;

    public static final void initInstance(final Context context, final List<IModel> modelList) {
        INSTANCE = new Presenter(context, modelList);
    }

    public static final Presenter getInst() {
        return INSTANCE;
    }

    private Presenter(final Context context, final List<IModel> modelList) {
        this.context = context;

        this.handlerMap = new HashMap<Callback, Handler>();
        this.modelHandlerList = new ArrayList<Handler>(modelList.size());
        this.viewHandlerList = new ArrayList<Handler>();

        for (int i = 0; i < modelList.size(); i++) {
            final HandlerThread handlerThread = new HandlerThread(modelList.get(i).getClass()
                    .getName());
            handlerThread.start();
            this.modelHandlerList
                    .add(new Handler(handlerThread.getLooper(), modelList.get(i)));
        }

        // TODO ezt a rész még nem vágom
        this.handlerThread = new HandlerThread(this.getClass().getCanonicalName());
        this.handlerThread.start();
        this.handler = new Handler(this.handlerThread.getLooper(), this);
    }

    public final void subscribe(final Callback viewComponentCallback) {
        final Handler handler = new Handler(viewComponentCallback);
        this.handlerMap.put(viewComponentCallback, handler);
        this.viewHandlerList.add(handler);
    }

    public final void unsubscribe(final Callback viewComponentallback) {
        final List<Handler> newList = new ArrayList<Handler>(this.viewHandlerList);
        newList.remove(this.handlerMap.get(viewComponentallback));
        this.handlerMap.remove(viewComponentallback);
        this.viewHandlerList = newList;
    }

    public final void dispose() {
        for (final Handler handler : this.modelHandlerList) {
            handler.getLooper().quit();
        }
    }

    @Override
    public boolean handleMessage(final Message msg) {
        return false;
    }

    public Context getApplicationContext() {
        return this.context;
    }

    public final void sendViewMessage(final int what, final int arg1, final int arg2,
            final Object obj, final Bundle bundle) {
        final List<Handler> outBoxList = this.viewHandlerList;
        for (final Handler handler : outBoxList) {
            this.sendMessageToTarget(handler, what, arg1, arg2, obj, bundle);
        }
    }

    public void sendViewMessage(final int what) {
        this.sendViewMessage(what, 0, 0, null, null);
    }

    public void sendViewMessage(final int what, final Object obj) {
        this.sendViewMessage(what, 0, 0, obj, null);
    }

    public void sendModelMessage(final int what) {
        this.sendModelMessage(what, 0, 0, null, null);
    }

    public void sendModelMessage(final int what, final Object obj) {
        this.sendModelMessage(what, 0, 0, obj, null);
    }

    public final void sendModelMessage(final int what, final int arg1, final int arg2,
            final Object obj, final Bundle bundle) {
        Log.d("presenter", "Presenter.sendModelMessage: START");
        for (final Handler handler : this.modelHandlerList) {
            this.sendMessageToTarget(handler, what, arg1, arg2, obj, bundle);
        }
        Message.obtain(this.handler, what, arg1, arg2, obj).sendToTarget();
        Log.d("presenter", "Presenter.sendModelMessage: END");
    }

    private void sendMessageToTarget(final Handler handler, final int what, final int arg1,
            final int arg2, final Object obj,
            final Bundle bundle) {
        final Message message = Message.obtain(handler, what, arg1, arg2, obj);
        message.setData(bundle);
        message.sendToTarget();
    }

}
