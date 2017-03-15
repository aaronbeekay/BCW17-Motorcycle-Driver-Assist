package com.bosch.myspin.fcsample;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent;

import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.ACTION_CLICK;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.ACTION_PRESS;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.ACTION_RELEASE;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_BACK;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_DPAD_DOWN;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_DPAD_LEFT;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_DPAD_RIGHT;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_DPAD_UP;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_KNOB_TICK_CCW;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_KNOB_TICK_CW;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_NEXT;
import static com.bosch.myspin.serversdk.focuscontrol.MySpinFocusControlEvent.KEYCODE_PREVIOUS;

/**
 * Default handler of mySPIN focus control events.
 *
 * @author com.bosch.softtec.myspin
 * @since 2016-05-04
 */
public class FocusInputHandler
{
    private static final String TAG = "mySPIN:FocusControl";

    /**
     * Finishes the activity when KEYCODE_BACK with ACTION_RELEASE or ACTION_CLICK is received.
     *
     * @param event
     *         the received focusControlEvent to handle.
     * @param activity
     *         the activity for which the focus should be handled.
     *
     * @return true if the activity was finished, false otherwise.
     */
    public static boolean finishActivityOnBack(Activity activity, MySpinFocusControlEvent event)
    {
        if ((event.getAction() == ACTION_RELEASE || event.getAction() == ACTION_CLICK)
                && event.getKeyCode() == KEYCODE_BACK)
        {
            Log.d(TAG, "FocusInputHandler/finishActivityOnBack");
            activity.finish();
            return true;
        }
        return false;
    }

    /**
     * Handle the focus according to the MySpinFocusControlEvent for the specified window.
     *
     * @param event
     *         the received focusControlEvent to handle.
     * @param window
     *         the window to handle the event for. It may be the window of an activity, or the window of a dialog
     */
    public static void handleFocusControlEvent(MySpinFocusControlEvent event, Window window)
    {
        Log.d(TAG, "FocusInputHandler/handleFocusControlEvent: " + event);

        if (handleClickEvent(event, window)) // this way we do not need to handle ACTION_CLICK events separately
        {
            return;
        }

        enableFocusMode(window);

        if (dispatchMySpinEvent(event, window))
        {
            return;
        }

        findNextFocus(event, window);
    }

    /**
     * Enables the focus mode.
     *
     * @param window
     *         the window to which the event should be handled.
     */
    private static void enableFocusMode(Window window)
    {
        if (window.getDecorView().isInTouchMode()) // deactivate touch mode, enable focus
        {
            Log.d(TAG, "FocusInputHandler/enableFocusMode, " + "enable focus");
            window.setLocalFocus(true, false);
        }
    }

    /**
     * Dispatches the MySpinFocusControlEvent for the specified activity.
     *
     * @param event
     *         the received focusControlEvent to handle.
     * @param window
     *         the window to which the event should be handled.
     *
     * @return true, if event was consumed, false otherwise.
     */
    private static boolean dispatchMySpinEvent(MySpinFocusControlEvent event, Window window)
    {
        if (event.getKeyCode() >= 1000) // only overwrite keyCode, if it's a custom keyCode
        {
            return dispatchKnobAnd2WayArrow(event, window, window.getCurrentFocus());

        }
        else
        {
            return dispatchKeyEvent(new KeyEvent(0, event.getEventTime(), event.getAction(), event.getKeyCode(), 0),
                    window);
        }
    }

    /**
     * Dispatches the KeyEvent for the specified window.
     *
     * @param event
     *         the created key event to handle.
     * @param window
     *         the window to which the event should be handled.
     *
     * @return true if event was consumed, false otherwise.
     */
    private static boolean dispatchKeyEvent(KeyEvent event, Window window)
    {
        return window.getDecorView().dispatchKeyEvent(event);
    }

    /**
     * Dispatches the MySpinFocusControlEvent while regarding the class of the currently focused event.
     *
     * @param event
     *         the received MySpinFocusControlEvent to handle.
     * @param window
     *         the window to which the event should be handled.
     * @param focusedView
     *         the currently focused view from which the dispatch will be handled.
     *
     * @return true if event was consumed, false otherwise.
     */
    private static boolean dispatchKnobAnd2WayArrow(MySpinFocusControlEvent event, Window window, View
            focusedView)
    {
        if (focusedView != null)
        {
            if (focusedView instanceof EditText)
            {
                Log.d(TAG, "FocusInputHandler/dispatchKnobAnd2WayArrow, " + "Dispatching for EditText");
                return dispatchForEditText(event, window, (EditText) focusedView);
            }
            else if (focusedView instanceof ListView)
            {
                Log.d(TAG, "FocusInputHandler/dispatchKnobAnd2WayArrow, " + "Dispatching for ListView");
                return dispatchForListView(event, window, (ListView) focusedView);
            }
            else if (focusedView instanceof GridView)
            {
                Log.d(TAG, "FocusInputHandler/dispatchKnobAnd2WayArrow, " + "Dispatching for GridView");
                return dispatchForGridView(event, window, (GridView) focusedView);
            }
            else if (focusedView instanceof ScrollView)
            {
                Log.d(TAG, "FocusInputHandler/dispatchKnobAnd2WayArrow, " + "Dispatching for ScrollView");
                return dispatchForScrollView(event, window, (ScrollView) focusedView);
            }
        }

        return false;
    }

    /**
     * Dispatches the MySpinFocusControlEvent with KEYCODE_DPAD_LEFT resp. KEYCODE_DPAD_RIGHT if the cursor is not at
     * the end, resp. the beginning of the text from the EditText, otherwise the event will not be handled.
     *
     * @param event
     *         the received focusControlEvent to handle.
     * @param window
     *         the window to which the event should be handled.
     * @param editText
     *         the currently focused view.
     *
     * @return true if event was consumed, false otherwise.
     */
    private static boolean dispatchForEditText(MySpinFocusControlEvent event, Window window, EditText editText)
    {
        switch (event.getKeyCode())
        {
            case KEYCODE_PREVIOUS:
            case KEYCODE_KNOB_TICK_CCW:
                if (editText.getSelectionStart() > 0)
                {
                    event.setKeyCode(KEYCODE_DPAD_LEFT);
                }
                else
                {
                    // Not handled, as the event cannot be interpreted as moving the text-cursor
                    return false;
                }
                break;

            case KEYCODE_NEXT:
            case KEYCODE_KNOB_TICK_CW:
                if (editText.getSelectionStart() < editText.getText().length())
                {
                    event.setKeyCode(KEYCODE_DPAD_RIGHT);
                }
                else
                {
                    // Not handled, as the event cannot be interpreted as moving the text-cursor
                    return false;
                }
                break;

            default:
                //Should not happen, we must consume this event, so the myspin keycode is not dispatched inside KeyEvent
                return true;
        }

        return dispatchKeyEvent(new KeyEvent(0, event.getEventTime(), event.getAction(), event.getKeyCode(), 0),
                window);
    }

    /**
     * Dispatches the MySpinFocusControlEvent with KEYCODE_DPAD_UP resp. KEYCODE_DPAD_DOWN if the currently focused
     * element is a ListView.
     *
     * @param event
     *         the received focusControlEvent to handle.
     * @param window
     *         the window to which the event should be handled.
     * @param listView
     *         the currently focused view.
     *
     * @return true if event was consumed, false otherwise.
     */
    private static boolean dispatchForListView(MySpinFocusControlEvent event, Window window, ListView listView)
    {
        switch (event.getKeyCode())
        {
            case KEYCODE_PREVIOUS:
            case KEYCODE_KNOB_TICK_CCW:
                event.setKeyCode(KEYCODE_DPAD_UP);
                break;
            case KEYCODE_NEXT:
            case KEYCODE_KNOB_TICK_CW:
                event.setKeyCode(KEYCODE_DPAD_DOWN);
                break;
            default:
                //Should not happen, we must consume this event, so the myspin keycode is not dispatched inside KeyEvent
                return true;
        }

        return dispatchKeyEvent(new KeyEvent(0, event.getEventTime(), event.getAction(), event.getKeyCode(), 0),
                window);
    }

    /**
     * Dispatches the MySpinFocusControlEvent with KEYCODE_DPAD_LEFT resp. KEYCODE_DPAD_RIGHT if the currently focused
     * element is a GridView, will automatically jump to the beginning if the last element is if focused.
     *
     * @param event
     *         the received focusControlEvent to handle.
     * @param window
     *         the window to which the event should be handled.
     * @param gridView
     *         the currently focused view.
     *
     * @return true if event was consumed, false otherwise.
     */
    private static boolean dispatchForGridView(MySpinFocusControlEvent event, Window window, GridView gridView)
    {
        // do not consume release event, logic executed on press event
        if (event.getAction() == ACTION_RELEASE)
        {
            return false;
        }

        if (event.getAction() == ACTION_PRESS)
        {
            int selectedPosition = gridView.getSelectedItemPosition();
            int count = gridView.getCount();

            switch (event.getKeyCode())
            {
                case KEYCODE_PREVIOUS:
                case KEYCODE_KNOB_TICK_CCW:
                    if (selectedPosition > 0)
                    {
                        gridView.setSelection(selectedPosition - 1); // move to the previous item

                        return true;
                    }
                    event.setKeyCode(KEYCODE_DPAD_UP);
                    break;

                case KEYCODE_NEXT:
                case KEYCODE_KNOB_TICK_CW:
                    if (selectedPosition < count - 1)
                    {
                        gridView.setSelection(selectedPosition + 1); // move to the next item
                        return true;
                    }

                    event.setKeyCode(KEYCODE_DPAD_DOWN);
                    break;

                default:
                    //Should not happen, we must consume this event, so the myspin keycode is not dispatched inside
                    //KeyEvent
                    return true;
            }
        }

        return false;
    }

    /**
     * Dispatches the MySpinFocusControlEvent with KEYCODE_DPAD_UP resp. KEYCODE_DPAD_DOWN if the currently focused
     * element is a ScrollView.
     *
     * @param event
     *         the received focusControlEvent to handle.
     * @param window
     *         the window to which the event should be handled.
     * @param scrollView
     *         the currently focused view.
     *
     * @return true if event was consumed, false otherwise.
     */
    private static boolean dispatchForScrollView(MySpinFocusControlEvent event, Window window, ScrollView scrollView)
    {
        switch (event.getKeyCode())
        {
            case KEYCODE_PREVIOUS:
            case KEYCODE_KNOB_TICK_CCW:
                event.setKeyCode(KEYCODE_DPAD_UP);
                break;
            case KEYCODE_NEXT:
            case KEYCODE_KNOB_TICK_CW:
                event.setKeyCode(KEYCODE_DPAD_DOWN);
                break;
            default:
                //Should not happen, we must consume this event, so the myspin keycode is not dispatched inside KeyEvent
                return true;
        }

        return dispatchKeyEvent(new KeyEvent(0, event.getEventTime(), event.getAction(), event.getKeyCode(), 0),
                window);
    }


    /**
     * If the dispatch failed, the FocusFinder will try to find the next focusable events.
     *
     * @param event
     *         the received focusControlEvent to handle.
     * @param window
     *         the window to which the event should be handled.
     *
     * @return true if event was consumed, false otherwise.
     */
    private static boolean findNextFocus(MySpinFocusControlEvent event, Window window)
    {
        // consume release event, logic will be executed on the press event
        if (event.getAction() == ACTION_RELEASE)
        {
            return false;
        }

        if (event.getAction() == ACTION_PRESS)
        {
            Log.d(TAG, "FocusInputHandler/findNextFocus, " + "Searching for next focusable element");

            int direction = -1;
            switch (event.getKeyCode())
            {
                case KEYCODE_DPAD_RIGHT:
                    direction = View.FOCUS_RIGHT;
                    break;

                case KEYCODE_DPAD_LEFT:
                    direction = View.FOCUS_LEFT;
                    break;

                case KEYCODE_DPAD_UP:
                    direction = View.FOCUS_UP;
                    break;

                case KEYCODE_DPAD_DOWN:
                    direction = View.FOCUS_DOWN;
                    break;

                case KEYCODE_PREVIOUS:
                case KEYCODE_KNOB_TICK_CCW:
                    direction = View.FOCUS_BACKWARD;
                    break;

                case KEYCODE_NEXT:
                case KEYCODE_KNOB_TICK_CW:
                    direction = View.FOCUS_FORWARD;
                    break;

                default:
            }

            if (direction > 0)
            {
                View nextFocus = FocusFinder.getInstance().findNextFocus(
                        (ViewGroup) window.getDecorView().getRootView(), window.getCurrentFocus(), direction);
                if (nextFocus != null)
                {
                    Log.d(TAG, "FocusInputHandler/findNextFocus, "
                            + "Request focus for next focusable view " + nextFocus);
                    return nextFocus.requestFocus();
                }
            }
        }

        return false;
    }

    /**
     * When replacing CLICK, with PRESS-RELEASE Pair, new events will have 200ms time difference.
     */
    private static final int PRESS_RELEASE_OFFSET = 200;

    /**
     * There must be at lest 50ms time difference between two click events.
     */
    private static final int MIN_TIME_BETWEEN_CLICKS = 50;

    /**
     * Timestamp of the lastly dispatched release event.
     */
    private static long lastReleaseEventTimestamp = -1;

    /**
     * Separate handler for dispatching touch events.
     */
    private static Handler touchEventsHandler = new Handler(Looper.getMainLooper());


    /**
     * If ACTION_CLICK is detected, it will get transformed into PRESS-RELEASE pair and dispatched for the further
     * processing.
     *
     * @param focusControlEvent
     *         original event.
     * @param window
     *         the window to handle the event for. It may be the window of an activity, or the window of a dialog.
     *
     * @return true if the ACTION_CLICK event was detected and dispatched as ACTION_PRESS and ACTION_RELEASE, false
     * otherwise.
     */
    private static synchronized boolean handleClickEvent(final MySpinFocusControlEvent focusControlEvent,
                                                         final Window window)
    {
        if (focusControlEvent.getAction() == ACTION_CLICK)
        {
            Log.d(TAG, "FocusInputHandler/handleClickEvent, "
                    + "Dispatching ACTION_CLICK event as ACTION_PRESS and ACTION_RELEASE");

            long clickEventTimestamp = focusControlEvent.getEventTime();
            int startDelay = 0;

            // modify PRESS and RELEASE actions timestamps, so that they will be having realistic time order
            // and adjust the start delay time (in case when new event should happen before the last release event)
            if (clickEventTimestamp - lastReleaseEventTimestamp < MIN_TIME_BETWEEN_CLICKS)
            {
                clickEventTimestamp = lastReleaseEventTimestamp + MIN_TIME_BETWEEN_CLICKS;
                startDelay = MIN_TIME_BETWEEN_CLICKS;
            }
            long releaseEventTime = clickEventTimestamp + PRESS_RELEASE_OFFSET;

            // create new event-pair
            final MySpinFocusControlEvent pressEvent = new MySpinFocusControlEvent(
                    ACTION_PRESS, focusControlEvent.getKeyCode(), clickEventTimestamp);
            final MySpinFocusControlEvent releaseEvent = new MySpinFocusControlEvent(
                    ACTION_RELEASE, focusControlEvent.getKeyCode(), releaseEventTime);

            lastReleaseEventTimestamp = releaseEventTime;

            // dispatch them with proper delay
            touchEventsHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    handleFocusControlEvent(pressEvent, window);
                }
            }, startDelay);

            touchEventsHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    handleFocusControlEvent(releaseEvent, window);
                }
            }, startDelay + PRESS_RELEASE_OFFSET);

            return true;
        }

        return false;
    }
}