package com.jacobmdavidson.computersms;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;

/**
 * Created by jacobdavidson
 */
public class ComputerSMSService extends Service{


    private ComputerSMSReceiver receiver;
    private TCPClient mTcpClient;
    private String ip = "";


    @Override
    public void onCreate() {
        super.onCreate();

        // Register the broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.MESSAGE.SMS_RECEIVED);
        filter.addAction(Constants.MESSAGE.CALL);
        receiver = new ComputerSMSReceiver();
        registerReceiver(receiver, filter);

        Log.i(Constants.DEBUGGING.LOG_TAG, "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // Start the foreground service
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(Constants.DEBUGGING.LOG_TAG, "Start Called");
            ip = intent.getStringExtra("ip");
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Notification notification =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(android.R.drawable.button_onoff_indicator_on)
                            .setContentTitle("ComputerSMS Running")
                            .setContentIntent(pendingIntent)
                            .setTicker("ComputerSMS Running").build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

            // Connect to the server
            new ConnectToServer().execute("");

            // Stop the foreground service
        } else if (intent.getAction().equals(Constants.ACTION.INCOMING_CALL_ACTION)) {
            Log.i(Constants.DEBUGGING.LOG_TAG, "Ringing");
            XmlSerializer xs = Xml.newSerializer();
            StringWriter sw = new StringWriter();
            try {
                xs.setOutput(sw);
                xs.startDocument("UTF-8", true);
                xs.startTag(null, "phoneCall");
                xs.startTag(null, "caller");
                xs.text(intent.getStringExtra("number"));
                xs.endTag(null, "caller");
                xs.endTag(null, "phoneCall");
                xs.endDocument();
            } catch(Exception e) {
                Log.i(Constants.DEBUGGING.LOG_TAG, e.toString());
            }
            Log.i(Constants.DEBUGGING.LOG_TAG, sw.toString());
            mTcpClient.sendMessage(sw.toString());


        } else if (intent.getAction().equals(Constants.ACTION.INCOMING_SMS_ACTION)) {
            Log.i(Constants.DEBUGGING.LOG_TAG, "SMS Received");
            XmlSerializer xs = Xml.newSerializer();
            StringWriter sw = new StringWriter();
            try {
                xs.setOutput(sw);
                xs.startDocument("UTF-8", true);
                xs.startTag(null, "smsMessage");
                xs.startTag(null, "number");
                xs.text(intent.getStringExtra("sender"));
                xs.endTag(null, "number");
                xs.startTag(null, "body");
                xs.text(intent.getStringExtra("body"));
                xs.endTag(null, "body");
                xs.endTag(null, "smsMessage");
                xs.endDocument();
            } catch(Exception e) {
                Log.i(Constants.DEBUGGING.LOG_TAG, e.toString());
            }
            Log.i(Constants.DEBUGGING.LOG_TAG, sw.toString());
            mTcpClient.sendMessage(sw.toString());

        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(Constants.DEBUGGING.LOG_TAG, "Stop Called");
            mTcpClient.stopClient();
            stopForeground(true);
            stopSelf();
        }

        // Add sms received
        // Add phone call received

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.i(Constants.DEBUGGING.LOG_TAG, "Service Destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class ConnectToServer extends AsyncTask<String, String, TCPClient> {


        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            }, ip);
            mTcpClient.run();

            return null;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.i(Constants.DEBUGGING.LOG_TAG, values[0]);
        }




    }
}
