package com.bosch.myspin.fcsample.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosch.myspin.fcsample.FocusInputHandler;
import com.bosch.myspin.fcsample.R;
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

/**
 * This activity demonstrates the usage of mySPIN focus control feature.
 *
 * @author Muhannad Fakhouri
 * @since 2016-11-14
 */
public class MainActivity extends Activity implements MySpinFocusControlListener, View.OnClickListener {

    private static final long CLICK_BLINK_DELAY = 200;

    private Fragment currentFragment;

    private TextView titleTv;
    private ImageView toggleIv;
    private ImageView backIv;
    private EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        // Register this application in the main launcher activity in the onCreate method to the
        // mySPIN ServerSDK.
        try {
            MySpinServerSDK.sharedInstance().registerApplication(getApplication());
        } catch (MySpinException e) {
            e.printStackTrace();
        }

        titleTv = (TextView) findViewById(R.id.title_tv);
        toggleIv = (ImageView) findViewById(R.id.toggle_layout_iv);
        backIv = (ImageView) findViewById(R.id.back_iv);
        searchBox = (EditText) findViewById(R.id.search_box);
        toggleIv.setOnClickListener(this);
        backIv.setOnClickListener(this);
        showPlayerFragment();
    }

    // Navigate to player fragment
    private void showPlayerFragment() {
        currentFragment = new PlayerFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_holder, currentFragment).commit();

        //update navigation toolbar
        backIv.setVisibility(View.GONE);
        toggleIv.setVisibility(View.VISIBLE);
        searchBox.setVisibility(View.GONE);
        toggleIv.setImageResource(R.drawable.playlist);
        titleTv.setText(R.string.now_playing);

        //As the player fragment is shown, the ForwardFocusId of the toggle button must be change
        //to the id of the first button of the player.
        toggleIv.setNextFocusForwardId(R.id.prev_iv);
    }

    // Navigate to playlist fragment
    private void showPlaylistFragment() {
        currentFragment = new PlayListFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_holder, currentFragment).commit();


        //update navigation toolbar
        backIv.setVisibility(View.VISIBLE);
        toggleIv.setVisibility(View.VISIBLE);
        searchBox.setVisibility(View.VISIBLE);
        toggleIv.setImageResource(R.drawable.album);
        titleTv.setText("");

        //As the playlist fragment is shown, the ForwardFocusId of the toggle button must be change
        //to the id of the list view.
        toggleIv.setNextFocusForwardId(R.id.playlist_listview);
    }

    // Navigate to albums grid fragment
    private void showAlbumsFragment() {
        currentFragment = new AlbumsFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_holder, currentFragment).commit();

        //update navigation toolbar
        backIv.setVisibility(View.VISIBLE);
        toggleIv.setVisibility(View.GONE);
        searchBox.setVisibility(View.GONE);
        titleTv.setText(R.string.albums);

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
                    backIv.setSelected(false);
                    if (currentFragment instanceof PlayListFragment)
                        showPlayerFragment();
                    else if (currentFragment instanceof AlbumsFragment)
                        showPlaylistFragment();
                    else
                        toggleIv.requestFocus();
                }
            }, CLICK_BLINK_DELAY);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toggle_layout_iv) {
            if (currentFragment != null) {
                if (currentFragment instanceof PlayerFragment)
                    showPlaylistFragment();
                else if (currentFragment instanceof PlayListFragment)
                    showAlbumsFragment();
            }
        } else if (v.getId() == R.id.back_iv) {
            handleBackButton();
        }
    }
}
