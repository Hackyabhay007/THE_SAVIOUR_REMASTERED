package com.example.thesaviour;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.net.URI;

public class MessageRecieveBroadcaster extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "10045";
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor prefeditor;
    String child1userid;
    String Parentchild1_Name;
    String alertcodeforparenthome = "19155";
    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        prefeditor = sharedPreferences.edit();
        //provided unique id which cant be matched with any received message
        child1userid = sharedPreferences.getString("Parentchild1UserId", "yiW1JfugvT\n" + "UJAOwcMm3l\n" + "Yiei38nPin\n" + "4KniXt856v\n" + "emP1xh33Ij\n" + "NYFxtsTjGs\n" + "YvvbR9Ftj3");
         Parentchild1_Name = sharedPreferences.getString("Parentchild1_Name","Your child");


        Bundle bundle =intent.getExtras();
        //pdus is the key for message it cant be changed
       Object [] smsobj = (Object[]) bundle.get("pdus");

        for (Object  obj:smsobj) {
            SmsMessage  message=  SmsMessage.createFromPdu((byte[]) obj );
          String mssg = message.getMessageBody();
            Log.d("Message"," Not Safe Message Received  Initiating Alerts ");
            if (mssg.contains(child1userid) &&mssg.length()>35)  {
                Toast.makeText(context, Parentchild1_Name + " Is in danger Open Saviour App to see where he IS ", Toast.LENGTH_SHORT).show();
                prefeditor.putString("alertforparentactivity",alertcodeforparenthome );
                prefeditor.apply();
                MediaPlayer mp = null;// Here
                mp = MediaPlayer.create(context, R.raw.highalarm);
                //Onreceive gives you context
                // Get the AudioManager
                //it sets the volume to max
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                // Set the volume of played media to maximum.
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
                //it starts the alarm system
                mp.start();
                NotificationForParentMode(context);
            }

            if (mssg.contains(child1userid) &&mssg.length()<40)  {
                Toast.makeText(context, Parentchild1_Name + " Is in danger Open Saviour App to see where he IS ", Toast.LENGTH_SHORT).show();
                MediaPlayer mp = null;// Here
                mp = MediaPlayer.create(context, R.raw.highalarm);
                //Onreceive gives you context
                // Get the AudioManager
                //it sets the volume to max
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                // Set the volume of played media to maximum.
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
                //it starts the alarm system
                mp.start();
                NotificationForUserMode(context);
            }

        }
    }

    void NotificationForParentMode(Context context)
    {
        Intent SafeHomeIntent = new Intent(context, parenthome.class);
        SafeHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, SafeHomeIntent, PendingIntent.FLAG_IMMUTABLE);
          final String NOTIFICATION_CHANNEL_ID = "PARENT_NOTIFICATION_CHANNEL";
          final int NOTIFICATION_ID = 123;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.gaurd1)
                .setContentTitle(Parentchild1_Name+ " Is Not Safe")
                .setContentText( Parentchild1_Name+" Has Initiated Danger Mode ,May Be not Safe "+"  Tap to See Activity")
                .setLights(0xff00ff00, 300, 100)
                .setColor(ContextCompat.getColor(context, R.color.red))
                .setColorized(true).setColor(Color.RED)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        // Create the notification channel (for devices running Android 26 or higher)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Parent Channel", importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Display the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    void NotificationForUserMode(Context context)
    {


        final String NOTIFICATION_CHANNEL_ID = "PARENT_NOTIFICATION_CHANNEL";
        final int NOTIFICATION_ID = 124;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.gaurd1)
                .setContentTitle("The Saviour Alert")
                .setContentText( "Ringing.....")
                .setLights(0xff00ff00, 300, 100)
                .setColor(ContextCompat.getColor(context, R.color.red))
                .setColorized(true).setColor(Color.RED)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        // Create the notification channel (for devices running Android 26 or higher)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "User Channel", importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Display the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
