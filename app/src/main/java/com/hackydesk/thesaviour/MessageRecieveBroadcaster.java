package com.hackydesk.thesaviour;

import android.app.Dialog;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import components.tools;

public class MessageRecieveBroadcaster extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "10045";
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor prefeditor;
    String child1userid;
    String Parentchild1_Name;
    String alertcodeforparenthome = "19155";
    String SAFE_CODE="??IAMSAFE??";
    String RING_CODE="$$RING$$???";
    String OFFLINE_DANGER_CODE = "OEFF!@#$L#@$I@#N$";
    String DANGER_CODE ="%%$***DANGER";//its only for primaryguardian
    String SOFT_CODE ="$2S0#4@$F&$#T";//its for non primary guardians
    String JOURNEY_CODE = "@#j(0ur{}ney}}mo+2de{";
    boolean muteswitch ;
    Dialog alertdialog ;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        prefeditor = sharedPreferences.edit();
        //provided unique id which cant be matched with any received message
        child1userid = sharedPreferences.getString("Parentchild1UserId", "yiW1JfugvT\n" + "UJAOwcMm3l\n" + "Yiei38nPin\n" + "4KniXt856v\n" + "emP1xh33Ij\n" + "NYFxtsTjGs\n" + "YvvbR9Ftj3");
         Parentchild1_Name = sharedPreferences.getString("Parentchild1_Name","Your child");
        muteswitch = sharedPreferences.getBoolean("MUTE_PARENT_ALARM",false);

        Bundle bundle =intent.getExtras();
        //pdus is the key for message it cant be changed
       Object [] smsobj = (Object[]) bundle.get("pdus");

        for (Object  obj:smsobj) {
            SmsMessage  message=  SmsMessage.createFromPdu((byte[]) obj );
          String mssg = message.getMessageBody();
            Log.d("Message"," Not Safe Message Received  Initiating Alerts ");
            if (mssg.contains(child1userid) && mssg.contains(DANGER_CODE))  {

                if (mssg.contains(OFFLINE_DANGER_CODE))
                {
                    notificationSound(1,context);

                    offlineSoshandler(mssg,context);
                }

                else {
                    Toast.makeText(context, Parentchild1_Name + " Initiated SOS Tap to See ", Toast.LENGTH_SHORT).show();
                    prefeditor.putString("alertforparentactivity",alertcodeforparenthome );
                    prefeditor.apply();
                    notificationSound(1,context);
                    //  alertdialog.show();
                    NotificationForParentMode(context);
                }
            }

            else if (mssg.contains(child1userid) && mssg.contains(SOFT_CODE))  {
                offlineSoshandler(mssg,context);
                Toast.makeText(context, Parentchild1_Name + " Initiated  SOS Mode Open App to See OverView ", Toast.LENGTH_SHORT).show();
                prefeditor.putString("alertforparentactivity",alertcodeforparenthome );
                prefeditor.apply();
               notificationSound(0,context);
                //  alertdialog.show();
                NotificationForParentMode(context);
            }


           else if (mssg.contains(RING_CODE))  {
              // Toast.makeText(context, Parentchild1_Name + "Initiated Danger Mode Open The Saviour App", Toast.LENGTH_SHORT).show();
               notificationSound(1,context);
                NotificationForUserMode(context);
            }

            else if (mssg.contains(child1userid) && mssg.contains(JOURNEY_CODE))  {
                // Toast.makeText(context, Parentchild1_Name + "Initiated Danger Mode Open The Saviour App", Toast.LENGTH_SHORT).show();
               notificationSound(0,context);
                NotificationForJourneyMode(context);
            }

           else if(mssg.contains(child1userid) && mssg.contains(SAFE_CODE)){
                NotificationForSafeMessage(context,Parentchild1_Name);
                 notificationSound(0,context);
                prefeditor.putString("alertforparentactivity","");
                prefeditor.apply();
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
                .setSmallIcon(R.drawable.mainlog_transparent_bg)
                .setContentTitle(Parentchild1_Name+ " Is Not Safe")
                .setContentText( Parentchild1_Name+" Has Initiated SOS Mode ,May Be not Safe "+"  Open to See Activity")
                .setLights(0xff00ff00, 300, 100)
                .setColor(ContextCompat.getColor(context, R.color.red))
                .setColorized(true).setColor(Color.RED)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        // Create the notification channel (for devices running Android 26 or higher)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "PARENT_NOTIFICATION_CHANNEL", importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Display the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    void NotificationForJourneyMode(Context context)
    {
        Intent SafeHomeIntent = new Intent(context, parenthome.class);
        SafeHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, SafeHomeIntent, PendingIntent.FLAG_IMMUTABLE);
        final String NOTIFICATION_CHANNEL_ID = "PARENT_NOTIFICATION_CHANNEL";
        final int NOTIFICATION_ID = 12344;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.mainlog_transparent_bg)
                .setContentTitle("The Saviour")
                .setContentText( Parentchild1_Name+" is Sharing Location With You")
                .setLights(0xff00ff00, 300, 100)
                .setColor(ContextCompat.getColor(context, R.color.hardtext))
                .setColorized(true).setColor(Color.RED)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        // Create the notification channel (for devices running Android 26 or higher)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "PARENT_NOTIFICATION_CHANNEL", importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Display the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    //If sos is activated through offline mode it will display a new notification with mssg and data



    void NotificationForUserMode(Context context)
    {


        final String NOTIFICATION_CHANNEL_ID = "PARENT_NOTIFICATION_CHANNEL";
        final int NOTIFICATION_ID = 124;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.mainlog_transparent_bg)
                .setContentTitle("The Saviour Alert")
                .setContentText( "Ringing.....")
                .setLights(0xff00ff00, 300, 100)
                .setColor(ContextCompat.getColor(context, R.color.bigbtnborder))
                .setColorized(true).setColor(Color.YELLOW)
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
        //remove notification after 15secons
        final int notificationId = ( int ) System. currentTimeMillis () ;
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Handler h = new Handler() ;
        long delayInMilliseconds = 15000 ;
        h.postDelayed( new Runnable() {
            public void run () {
                notificationManager .cancel( notificationId ) ;
            }
        } , delayInMilliseconds) ;
    }


    void NotificationForSafeMessage(Context context,String ChildName)
    {
        final String NOTIFICATION_CHANNEL_ID = "PARENT_NOTIFICATION_CHANNEL_SAFE_NOW";
        final int NOTIFICATION_ID = 124089;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.mainlog_transparent_bg)
                .setContentTitle("The Saviour | SAFE MESSAGE ")
                .setContentText(ChildName + " is Safe Now")
                .setLights(0xff00ff00, 300, 100)
                .setColor(ContextCompat.getColor(context, R.color.btnbackgrouncolor))
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

    void offlineSoshandler(String mssg, Context context)
    {
        //decode mssg
        String data = mssg;
                String result = data.substring(data.indexOf("{[")+2,data.length()-2);
                String arr[]=result.split(",");
                String Battery = arr[0];
                String Latitude = arr[1];
                String longitude =arr[2];
                Savedatatolocaldb(Battery,Latitude,longitude);

                Intent SafeHomeIntent = new Intent(context, offlineSosActivity.class);
                SafeHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, SafeHomeIntent, PendingIntent.FLAG_IMMUTABLE);
                final String NOTIFICATION_CHANNEL_ID = "PARENT_NOTIFICATION_CHANNEL_OFFLINE_SOS";
                final int NOTIFICATION_ID = 124089;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.mainlog_transparent_bg)
                        .setContentTitle("The Saviour | OFFLINE SOS ")
                        .setContentText(Parentchild1_Name+ " is not Safe" + "Open To See Details")
                        .setLights(0xff00ff00, 300, 100)
                        .setColor(ContextCompat.getColor(context, R.color.red))
                        .setColorized(true).setColor(Color.RED)
                        .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent);
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


    //it contains 2 frequency 0 and 1 0 - small beep // 1 - for long beep full sound
    void notificationSound(int frequency,Context context)
    {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);
        AudioManager audioManager =
                (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        if (!muteswitch)
      {
          MediaPlayer mp = null;// Here
          if(frequency==1)
          {
             // Toast.makeText(context, "called", Toast.LENGTH_SHORT).show();
              MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.highalarm);
              mediaPlayer.start();
          }

          if (frequency==0)
          {
              try {
                  mp = MediaPlayer.create(context, R.raw.beepwarning);
                  //it sets the volume to max
                  mp.start();

              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      }
    }

    void Savedatatolocaldb(String battery ,String lat ,String lng)
    {
        tools t1 = new tools();
        prefeditor.putString("CHILD1_NAME",Parentchild1_Name);
        prefeditor.putString("CHILD1_BATTERY_DATA",battery);
        prefeditor.putString("CHILD1_LAT",lat);
        prefeditor.putString("CHILD1_LNG",lng);
        prefeditor.putString("CHILD1_SOS_TIME",t1.getCurrentTimeWithoutAMPM());
        prefeditor.putString("CHILD1_SOS_DATE",t1.getCurrentDate());
        prefeditor.apply();
    }


}
