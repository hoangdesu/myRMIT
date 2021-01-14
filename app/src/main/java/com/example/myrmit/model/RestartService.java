package com.example.myrmit.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.myrmit.MainActivity;

public class RestartService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MyService mYourService = new MyService();
        MainActivity.mServiceIntent = new Intent(context, mYourService.getClass());
        context.startService(MainActivity.mServiceIntent);
    }
}
