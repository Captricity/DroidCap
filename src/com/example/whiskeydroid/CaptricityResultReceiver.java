package com.example.whiskeydroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/* http://stackoverflow.com/questions/3197335/restful-api-service */
public class CaptricityResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public CaptricityResultReceiver (Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}