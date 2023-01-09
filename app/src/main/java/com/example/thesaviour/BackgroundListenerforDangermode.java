package com.example.thesaviour;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BackgroundListenerforDangermode extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //check the intent something like:
//        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
//            int newVolume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
//            int oldVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);
//
//            if (newVolume != oldVolume) {
//                Toast.makeText(context ,"newVolume" +newVolume + " oldVolume" + oldVolume, Toast.LENGTH_SHORT).show();
//                System.out.println("In onReceive" + "newVolume" +newVolume + " oldVolume" + oldVolume );
//
//            }
//        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
}