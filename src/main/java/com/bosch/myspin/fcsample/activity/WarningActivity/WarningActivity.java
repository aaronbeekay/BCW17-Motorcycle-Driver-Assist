package com.bosch.myspin.fcsample.activity.WarningActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bosch.myspin.fcsample.R;
import com.bosch.myspin.fcsample.activity.MainActivity;
import com.bosch.myspin.serversdk.MySpinServerSDK;

public class WarningActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        // Check what message we got and update the string appropriately
        Intent intent = getIntent();
        int warning_type = intent.getIntExtra(MainActivity.WARNING_TYPE, 0);

        TextView warning_text_view = (TextView) findViewById(R.id.warningText);

        switch(warning_type){
            case MainActivity.WARNING_TYPE_DANGEROUSINTERSECTION:
                warning_text_view.setText(R.string.warning_dangerous_intersection);
                break;
            case MainActivity.WARNING_TYPE_DECREASINGRADIUS:
                warning_text_view.setText(R.string.warning_decreasing_radius);
                break;
            case MainActivity.WARNING_TYPE_SHARPTURN:
                warning_text_view.setText(R.string.warning_sharp_turn);
                break;
        }
    }

    @Override
    protected void onResume() {
        //registerReceiver(broadcastReceiver, new IntentFilter("com.bosch.myspin.fcsample.CLOSE_WARNING"));
        super.onResume();

        // registers a FocusControlListener, which listens to the keys that might
        // change focus state
        //MySpinServerSDK.sharedInstance().setFocusControlListener(this);
    }
/*
    private BroadcastReceiver broadcastReceiver() = new BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
    */

    @Override
    protected void onPause() {
        super.onPause();

        // When this activity gets stopped unregister for mySPIN connection events and focus control listener.
        MySpinServerSDK.sharedInstance().removeFocusControlListener();
    }
}
