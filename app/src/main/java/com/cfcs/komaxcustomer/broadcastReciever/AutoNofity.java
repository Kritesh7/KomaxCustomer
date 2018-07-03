package com.cfcs.komaxcustomer.broadcastReciever;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.cfcs.komaxcustomer.config_customer.Config_Customer;

public class AutoNofity extends Service {

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

   //   Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        CustomerBroadcastReciever.startAlarm(getBaseContext());
        CustomerBroadcastReciever.enableBroadcastReceiver(getBaseContext());


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
      //  Toast.makeText(this, "Service Destroy", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}