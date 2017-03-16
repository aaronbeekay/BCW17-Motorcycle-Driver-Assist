package com.bosch.myspin.fcsample.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bosch.myspin.fcsample.FocusInputHandler;
import com.bosch.myspin.fcsample.GCMListenerService;
import com.bosch.myspin.fcsample.R;
import com.bosch.myspin.fcsample.RegistrationIntentService;
import com.bosch.myspin.fcsample.activity.WarningActivity.WarningActivity;
import com.bosch.myspin.fcsample.fragment.AlbumsFragment;
import com.bosch.myspin.fcsample.fragment.PlayListFragment;
import com.bosch.myspin.fcsample.fragment.PlayerFragment;
import com.bosch.myspin.serversdk.MySpinException;
import com.bosch.myspin.serversdk.MySpinServerSDK;
import com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent;
import com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlListener;

import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.ACTION_CLICK;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.ACTION_RELEASE;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_BACK;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_DPAD_DOWN;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_DPAD_LEFT;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_DPAD_RIGHT;

/**
 * This activity demonstrates the usage of mySPIN focus control feature.
 *
 * @author Muhannad Fakhouri
 * @since 2016-11-14
 */

public class MainActivity extends Activity implements MySpinFocusControlListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    // Values used to  pass warning type to WarningActivity
    public static final String WARNING_TYPE = "com.bosch.motorcycleda.WARNING_TYPE";
    public static final int WARNING_TYPE_DANGEROUSINTERSECTION = 1;
    public static final int WARNING_TYPE_SHARPTURN = 2;
    public static final int WARNING_TYPE_DECREASINGRADIUS = 3;

    private static final long CLICK_BLINK_DELAY = 200;

    private final String url = "https://s3.eu-central-1.amazonaws.com/bcw2017/bcw2017_bucket2.txt";

    private Fragment currentFragment;

    private TextView titleTv;
    private ImageView toggleIv;
    private ImageView backIv;
    private EditText searchBox;

    private Handler handler;
    private RequestQueue queue;

    public int current_warning;

    public int get_current_warning(){
        return current_warning;
    }

    /* Determine which warning to display and call the activity which displays it to the rider. */
    public void displayWarning(int warning_type) {
        if (warning_type != current_warning) {
            current_warning = warning_type;
            Intent intent = new Intent(this, WarningActivity.class);
            intent.putExtra(WARNING_TYPE, warning_type);
            startActivity(intent);
        }
    }

    /* Dismiss whatever warning is currently up */
    public void closeWarning(){
        
    }

    String lastResponse = "";
    /* Get the latest value from the server */
    Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            try {
                // request a string response from that url
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Got string back " + response);
                        if(lastResponse.equals(response)){
                            return;
                        }
                        lastResponse = response;
                        switch(response) {
                            case "dangerous_intersection":
                                displayWarning(WARNING_TYPE_DANGEROUSINTERSECTION);
                                break;
                            case "sharp_turn":
                                displayWarning(WARNING_TYPE_SHARPTURN);
                                break;
                            case "decreasing_radius":
                                displayWarning(WARNING_TYPE_DECREASINGRADIUS);
                                break;
                            default:
                                Log.d(TAG, "Invalid response from control file");
                                closeWarning();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, "Error in Volley request");
                    }
                });

                queue.add(stringRequest);
            } finally {
                handler.postDelayed(runnableCode, 2000);
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "Main instance starting up");

        /* disabled: GCM stuff
        Intent mServiceIntent = new Intent(this, RegistrationIntentService.class);
        startService(mServiceIntent);
        Intent mListenerIntent = new Intent(this, GCMListenerService.class);
        startService(mListenerIntent);
        */

        queue = Volley.newRequestQueue(this);
        handler = new Handler();
        handler.post(runnableCode);

        // Register this application in the main launcher activity in the onCreate method to the
        // mySPIN ServerSDK.
        try {
            MySpinServerSDK.sharedInstance().registerApplication(getApplication());
        } catch (MySpinException e) {
            e.printStackTrace();
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
    public void onBackPressed() {
        //we override onBackPressed in order to disable the back button
        if (!MySpinServerSDK.sharedInstance().isConnected())
            super.onBackPressed();
    }

    @Override
    public void onFocusControlEvent(MySpinFocusControlEvent mySpinFocusControlEvent) {

        //TODO check if dialog is visible and forward the event to the dialog
        //        FocusInputHandler.handleFocusControlEvent(mySpinFocusControlEvent, dialog.getWindow());

        if ((mySpinFocusControlEvent.getAction() == ACTION_RELEASE || mySpinFocusControlEvent.getAction() == ACTION_CLICK)
                && mySpinFocusControlEvent.getKeyCode() == KEYCODE_BACK)
        {
            handleBackButton();
        } else {
            FocusInputHandler.handleFocusControlEvent(mySpinFocusControlEvent, getWindow());
        }

    }

    /**
     * Handles the mySPIN back focus control event
     */
    private void handleBackButton() {

        if(searchBox.isFocused()){
            if (!(searchBox.getSelectionEnd() == 0 && searchBox.getSelectionStart() == 0))
            {
                searchBox.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0));
                searchBox.dispatchKeyEvent(new KeyEvent(0, 100, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0));
                return;
            }
        }

        if (currentFragment != null) {
            if (toggleIv.getVisibility() == View.VISIBLE && !(toggleIv.isFocused() || searchBox.isFocused())) {
                toggleIv.requestFocus();
                return;
            }
            backIv.setSelected(true);
            //We select the back button, and perform the action after CLICK_BLINK_DELAY.
            //So the user sees that the back button is focused before back is performed.
            backIv.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, CLICK_BLINK_DELAY);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toggle_layout_iv) {
            if (currentFragment != null) {

            }
        } else if (v.getId() == R.id.back_iv) {
            handleBackButton();
        }
    }
}
