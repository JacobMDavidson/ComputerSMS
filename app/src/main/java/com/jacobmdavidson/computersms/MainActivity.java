package com.jacobmdavidson.computersms;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    // The enable/disable toggle button
    private ToggleButton toggle;

    // Shared preferences that stores the toggle state
    private SharedPreferences sharedPrefs;

    // Shared preferences editor
    private SharedPreferences.Editor editor;

    // Computer IP Text
    private EditText computerIPAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the saved preferences
        sharedPrefs = getSharedPreferences("com.jacobmdavidson.computersms", MODE_PRIVATE);
        editor = sharedPrefs.edit();

        // Instantiate and Set the toggle button according to the preferences
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setChecked(sharedPrefs.getBoolean(Constants.TOGGLE_BUTTON.STATE, false));

        // Instantiate the EditText object for the IP Address
        computerIPAddress = (EditText)findViewById(R.id.computerIP);
    }

    /**
     * Update the shared prefs and start/stop the service when toggle button is pressed.
     * @param view the view from which the click is received
     */
    public void onToggleClicked(View view) {
        boolean enabled = ((ToggleButton) view).isChecked();
        if (enabled) {
            // Update the shared prefs
            editor.putBoolean(Constants.TOGGLE_BUTTON.STATE, true);
            editor.commit();

            Intent startIntent = new Intent(MainActivity.this, ComputerSMSService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            String ipAddress =  computerIPAddress.getText().toString();
            startIntent.putExtra("ip", ipAddress);
            startService(startIntent);

        } else {

            // Update the shared prefs
            editor.putBoolean(Constants.TOGGLE_BUTTON.STATE, false);
            editor.commit();

            Intent stopIntent = new Intent(MainActivity.this, ComputerSMSService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            startService(stopIntent);
        }
    }
}
