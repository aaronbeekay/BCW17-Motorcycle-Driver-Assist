package com.bosch.myspin.fcsample.activity.WarningActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bosch.myspin.fcsample.R;
import com.bosch.myspin.fcsample.activity.MainActivity;
import com.bosch.myspin.serversdk.MySpinServerSDK;
import com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent;
import com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlListener;

import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.ACTION_RELEASE;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_DPAD_LEFT;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_DPAD_RIGHT;

public class WarningActivity extends Activity implements MySpinFocusControlListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        // code for close button
//        Button closeButton = (Button) findViewById(R.id.bClose);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

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
        super.onResume();

        // registers a FocusControlListener, which listens to the keys that might
        // change focus state
        MySpinServerSDK.sharedInstance().setFocusControlListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // When this activity gets stopped unregister for mySPIN connection events and focus control listener.
        MySpinServerSDK.sharedInstance().removeFocusControlListener();
    }

    @Override
    public void onFocusControlEvent(MySpinFocusControlEvent mySpinFocusControlEvent) {
        if (mySpinFocusControlEvent.getAction() == ACTION_RELEASE){
            if (mySpinFocusControlEvent.getKeyCode() == KEYCODE_DPAD_RIGHT || mySpinFocusControlEvent.getKeyCode() == KEYCODE_DPAD_LEFT){
                //dismiss the notification
                finish();
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
