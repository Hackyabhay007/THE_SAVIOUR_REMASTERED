package com.hackydesk.thesaviour;

import static android.content.ContentValues.TAG;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class DangerModeBackgroundListener extends AccessibilityService {
    SharedPreferences sharedPreferences ;
    int Trigger = 7;
    private int count = 0;
    private long startMillis=0;

    public DangerModeBackgroundListener() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        sharedPreferences = getApplicationContext().getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        Log.d(TAG, "onAccessibilityEvent" + accessibilityEvent.toString());
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "service is interrupted");
    }


    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        return handleKeyEvent(event);
    }

    private boolean handleKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (count==Trigger) {
                  //  StartDangerMode();
                }
                count++;
                Log.d("Check", "KeyUp"+count);
                //Toast.makeText(this, String.valueOf(count), Toast.LENGTH_SHORT).show();
            }
            else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d("Check", "KeyDown");
                long time= System.currentTimeMillis();
                //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
                if (startMillis==0 || (time-startMillis> 4000) ) {
                    startMillis=time;
                    count=0;
                }
                //it is not the first, and it has been  less than 3 seconds since the first
                else{ //  time-startMillis< 3000
                    count++;
                }
                if (count==Trigger) {
                    StartDangerMode();
                    //Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        }
        return super.onKeyEvent(event);
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "service is connected");
    }

    void StartDangerMode()
    {
        ValidteSettings();
    }

    void ValidteSettings()
    {
       boolean accessibilityServiceEnabled = isAccessibilityServiceEnabled(getApplicationContext(), DangerModeBackgroundListener.class);
        if (accessibilityServiceEnabled)
        {
            Intent serviceIntent = new Intent(this, DangerModeService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Toast.makeText(this, "Starting Danger Mode", Toast.LENGTH_SHORT).show();
                startForegroundService(serviceIntent);
            }
        }
        else{
            Log.e(TAG, "RAPID SOS And Accessibility Permission IS DEACTIVATED");
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }



}