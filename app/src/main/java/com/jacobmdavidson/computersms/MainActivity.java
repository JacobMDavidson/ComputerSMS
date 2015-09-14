package com.jacobmdavidson.computersms;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class MainActivity extends AppCompatActivity {

    // The enable/disable toggle button
    private ToggleButton toggle;

    // Computer Identification Text
    private TextView serviceDescription;

    // Server IP Address
    private String ipAddress;

    // Server port number
    private int portNumber;

    // mDNS service
    private JmDNS jmdns;

    private JmDNSClient jmDNSClient;

    // Service type string
    public final static String SERVICE_TYPE = "_http._tcp.local.";


    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(Looper.getMainLooper());

        // Instantiate and Set the toggle button according to the preferences
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setChecked(false);
        toggle.setEnabled(false);

        // Instantiate the EditText object for the IP Address
        serviceDescription = (TextView)findViewById(R.id.textView1);

        jmDNSClient = new JmDNSClient();
        jmDNSClient.start();

    }

    /**
     * Update the shared prefs and start/stop the service when toggle button is pressed.
     * @param view the view from which the click is received
     */
    public void onToggleClicked(View view) {
        boolean enabled = ((ToggleButton) view).isChecked();
        if (enabled) {

            Intent startIntent = new Intent(MainActivity.this, ComputerSMSService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startIntent.putExtra("ip", ipAddress);
            startIntent.putExtra("port", portNumber);
            startService(startIntent);
            serviceDescription.setText("Service Started!");


        } else {
            serviceDescription.setText("");
            Intent stopIntent = new Intent(MainActivity.this, ComputerSMSService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            startService(stopIntent);
        }
    }



    private class JmDNSClient extends Thread {


        @Override
        public void run() {
            super.run();
            try {
                Log.i(Constants.DEBUGGING.LOG_TAG, "getting ip address");
                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                // get the device ip address
                final InetAddress deviceIpAddress = getDeviceIpAddress(wifi);

                Log.i(Constants.DEBUGGING.LOG_TAG, "creating JMDNS");
                jmdns = JmDNS.create(deviceIpAddress, "ComputerSMS");

                Log.i(Constants.DEBUGGING.LOG_TAG, "Adding JMDNS service listener");
                jmdns.addServiceListener(SERVICE_TYPE, new SampleListener());
                Log.i(Constants.DEBUGGING.LOG_TAG, "jmDNS Service listener added");


            } catch (Exception e) {
                Log.i(Constants.DEBUGGING.LOG_TAG, e.toString());

            }

        }

        private class SampleListener implements ServiceListener {
            @Override
            public void serviceAdded(ServiceEvent event) {

                Log.i(Constants.DEBUGGING.LOG_TAG, "serviceAdded");
                ServiceInfo info = jmdns.getServiceInfo(SERVICE_TYPE, event.getName());
                if(info.getName().equals("ComputerSMS")) {

                    Log.i(Constants.DEBUGGING.LOG_TAG, "updating");
                    startService(info);


                }

            }

            @Override
            public void serviceRemoved(ServiceEvent event) {
                Log.i(Constants.DEBUGGING.LOG_TAG, "Service Removed");
            }

            @Override
            public void serviceResolved(ServiceEvent event) {
                Log.i(Constants.DEBUGGING.LOG_TAG, "serviceResolved");
                ServiceInfo info = event.getInfo();
                if(info.getName().equals("ComputerSMS")) {

                    Log.i(Constants.DEBUGGING.LOG_TAG, "Updating");
                    startService(info);


                }
            }

            public void startService (ServiceInfo info) {
                Log.i(Constants.DEBUGGING.LOG_TAG, "startService called");
                portNumber = info.getPort();
                ipAddress = info.getHostAddresses()[0];
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        toggle.setEnabled(true);
                        serviceDescription.setText(ipAddress);
                    }
                });
                jmdns.removeServiceListener(SERVICE_TYPE, this);
                try {
                    jmdns.close();
                    Log.i(Constants.DEBUGGING.LOG_TAG, "jmdns closed");
                } catch ( Exception e) {
                    Log.i(Constants.DEBUGGING.LOG_TAG, e.toString());
                }


            }

        }
    }

    /**
     * Gets the current Android device IP address or return 10.0.0.2 which is localhost on Android.
     * <p>
     * @return the InetAddress of this Android device
     */
    private InetAddress getDeviceIpAddress(WifiManager wifi) {
        InetAddress result = null;
        try {
            // default to Android localhost
            result = InetAddress.getByName("10.0.0.2");

            // figure out our wifi address, otherwise bail
            WifiInfo wifiinfo = wifi.getConnectionInfo();
            int intaddr = wifiinfo.getIpAddress();
            byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff) };
            result = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException ex) {
            Log.w(Constants.DEBUGGING.LOG_TAG, String.format("getDeviceIpAddress Error: %s", ex.getMessage()));
        }

        return result;
    }
}
