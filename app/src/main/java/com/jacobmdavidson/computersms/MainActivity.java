package com.jacobmdavidson.computersms;

import android.content.Intent;
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
                Log.i(Constants.DEBUGGING.LOG_TAG, "caatfytfuyt");
                jmdns = JmDNS.create();
                jmdns.addServiceListener(SERVICE_TYPE, new SampleListener());



            } catch (Exception e) {
                Log.i(Constants.DEBUGGING.LOG_TAG, e.toString());

            }

        }

        private class SampleListener implements ServiceListener {
            @Override
            public void serviceAdded(ServiceEvent event) {

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
                ServiceInfo info = event.getInfo();
                if(info.getName().equals("ComputerSMS")) {

                    Log.i(Constants.DEBUGGING.LOG_TAG, "Updating");
                    startService(info);


                }
            }

            public void startService (ServiceInfo info) {
                portNumber = info.getPort();
                ipAddress = info.getHostAddresses()[0];
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        toggle.setEnabled(true);
                    }
                });
                jmdns.removeServiceListener(SERVICE_TYPE, this);
                try {
                    jmdns.close();
                } catch ( Exception e) {
                    Log.i(Constants.DEBUGGING.LOG_TAG, e.toString());
                }


            }

        }
    }
}
