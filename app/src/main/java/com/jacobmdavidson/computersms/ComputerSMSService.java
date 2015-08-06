package com.jacobmdavidson.computersms;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by jacobdavidson
 */
public class ComputerSMSService extends Service{
    private static final String LOG_TAG = "ComputerSMSService";

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.MESSAGE.SMS_RECEIVED);
        filter.addAction(Constants.MESSAGE.CALL);
        registerReceiver(receiver, filter);
        Log.i(LOG_TAG, "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Start Called");
            Intent notificationIntent = new Intent (this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Notification notification =
                    new NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.button_onoff_indicator_on)
                    .setContentTitle("ComputerSMS Running")
                    .setContentIntent(pendingIntent)
                    .setTicker("ComputerSMS Running").build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Stop Called");
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.i(LOG_TAG, "Service Destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Add a broadcast receiver
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // SMS message is received
            if(action.equals(Constants.MESSAGE.SMS_RECEIVED)) {
                Log.i(LOG_TAG, "SMS Received");

            // Phone call is received
            } else if (action.equals(Constants.MESSAGE.CALL)) {

                Bundle bundle = intent.getExtras();
                String state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    Log.i(LOG_TAG, "Ringing");
                }

            }
        }
    };
}
