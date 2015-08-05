package com.jacobmdavidson.computersms;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    // The enable/disable toggle button
    private ToggleButton toggle;

    // Shared preferences that stores the toggle state
    private SharedPreferences sharedPrefs;

    // Shared preferences editor
    private SharedPreferences.Editor editor;

    // Package Manager used to register/deregister the MessageReceiver
    private PackageManager pm;

    // Component to register/deregister
    private ComponentName component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the saved preferences
        sharedPrefs = getSharedPreferences("com.jacobmdavidson.computersms", MODE_PRIVATE);
        editor = sharedPrefs.edit();

        // Instantiate and Set the toggle button according to the preferences
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setChecked(sharedPrefs.getBoolean("ToggleButtonState", false));

        // get the package manager, and instantiate the component name
        pm = this.getPackageManager();
        component = new ComponentName(this, MessageReceiver.class);
    }

    /**
     * Update the shared prefs and enable/disable the MessageReceiver when the
     * toggle button is pressed.
     * @param view the view from which the click is received
     */
    public void onToggleClicked(View view) {
        boolean enabled = ((ToggleButton) view).isChecked();
        if (enabled) {

            // Update the shared prefs
            editor.putBoolean("ToggleButtonState", true);
            editor.commit();

            // Enable the receiver
            pm.setComponentEnabledSetting(component,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {

            // Update the shared prefs
            editor.putBoolean("ToggleButtonState", false);
            editor.commit();

            // Disable the receiver
            pm.setComponentEnabledSetting(component,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
