package com.hackydesk.thesaviour;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import components.functiontools;
import components.tools;

//reference
//https://medium.com/@Codeible/understanding-and-using-services-in-android-background-foreground-services-8130f6bbf2a5#:~:text=To%20create%20a%20Background%20Service,startService()%20or%20startForegroundService().
public class DangerModeService extends Service implements connectionchecker.ReceiverListener  {

    Context mContext;
    TextView output;
    String lastlocationlink;
    DangerModeService startForegroundService;
    String bnum2;
    String bnum3;
    String  authuserid;
    FusedLocationProviderClient fusedLocationProviderClient;
    String primaryphnno;
    String primarybname;
    String DANGER_MODE_CODE="%%$***DANGER";
    String garbageString;
    Integer livelocationrefresh;
    LocationRequest locationRequest;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    NotificationManager notificationManager;
    String OFFLINE_DANGER_CODE = "OEFF!@#$L#@$I@#N$";//if internet off

    String DANGER_CODE ="%%$***DANGER";//its only for primaryguardian
    String SOFT_CODE ="$2S0#4@$F&$#T";//its for non primary guardians

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        sharedPreferences = mContext.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        primarybname = sharedPreferences.getString("primarytext", "name");
        primaryphnno = sharedPreferences.getString("primaryphnno", "100");
        bnum2 = sharedPreferences.getString("bnum2", "100");
        bnum3 = sharedPreferences.getString("bnum3", "100");
        notificationManager = getSystemService(NotificationManager.class);


       initiateDangermode();

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        Notification.Builder notification = null;
        Intent SafeHomeIntent = new Intent(this, homie.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, SafeHomeIntent, PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNELID)
                    .setContentText("Initiated All Alerts")
                    .setContentTitle("SOS Mode enabled")
                    .setSmallIcon(R.drawable.mainlog_transparent_bg)
                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.red))
                    .setColorized(true).setColor(Color.RED)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);

        }
        assert notification != null;
        startForeground(1001, notification.build());
        return START_NOT_STICKY;
       // return super.onStartCommand(intent, flags, startId);
    }

    void initiateDangermode()
    {
        Toast.makeText(mContext, "STARTING........", Toast.LENGTH_SHORT).show();
        //checks connection and execute further
        checkConnection();

    }

    public void checkConnection() {
        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();
        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
        // Initialize listener
        connectionchecker.Listener = this;
        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        //sending what to do when net  on
        connectionstate(isConnected);
    }


    void connectionstate(boolean connectionstate) {
        if (connectionstate) {
          //  Toast.makeText(mContext, "SENDING LOCATION ......", Toast.LENGTH_SHORT).show();
            //calls and sends current co ordinates to server when net on
            smssender(primaryphnno, bnum2, bnum3, "");
            deviceStatus();
            sentSosNotification(0);
        } else {
            //calls when net off
            offlineSos();
        }
    }


    void smssender(String primaryphnno, String bnum2, String bnum3, String Message) {
        final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Cant sent Alert To Parent SMS PERMISSION OFF", Toast.LENGTH_SHORT).show();
           // Toast.makeText(mContext, "Give Sms Permissions From Settings", Toast.LENGTH_SHORT).show();
        } else {
            // Set the destination phone number to the string in editText.
            // Get the text of the SMS message.
            String Primapryparentmessage = DANGER_CODE+currentuserid()+Message;
            String Nonprimaryparentmessage = DANGER_CODE+currentuserid()+Message;
            // Set the service center address if needed, otherwise null.
            String scAddress = null;
            // Set pending intents to broadcast
            // when message sent and when delivered, or set to null.
            PendingIntent sentIntent = null, deliveryIntent = null;
            // Use SmsManager.
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage
                    (primaryphnno, null, Primapryparentmessage,
                            null, null);
          //  Toast.makeText(mContext, "SENDING MESSAGE TO : " + primaryphnno, Toast.LENGTH_SHORT).show();

          if(!Objects.equals(primaryphnno, bnum2))
          {
             smsManager.sendTextMessage
                      (bnum2, null, Nonprimaryparentmessage,
                              null, null);
           //   Toast.makeText(mContext, "SENDING MESSAGE TO : " + bnum2, Toast.LENGTH_SHORT).show();
          }
          if (!Objects.equals(bnum3, primaryphnno))
          {
              if(!Objects.equals(bnum3, bnum2)){
                  smsManager.sendTextMessage
                      (bnum3, null, Nonprimaryparentmessage,
                              null, null);
            //     Toast.makeText(mContext, "SENDING MESSAGE TO : " + bnum3, Toast.LENGTH_SHORT).show();
              }
//
          }
        }
    }


    void sentSosNotification(int code)
    {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        String childname = sharedPreferences.getString("profile_name","User");
//code 0 for off
//code 1 for on
        functiontools journeynotification = new functiontools(mContext);
        if(code==0)
        {
            journeynotification.sendOnlineMesageToparent("The Saviour",childname + " Initiated SOS mode Tap To See Details");
        }
        if (code==1)
        {
            journeynotification.sendOnlineMesageToparent("The Saviour",childname + " Is Safe Now , used SAFE MODE");
        }

    }

    //offlinemode with data
    String  OFFLINE_MESSAGE;

    void offlineSos() {
        //data formatting
        //038//latitude//longitude
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        Toast.makeText(mContext, "Internet off Initiating Offline Sos Mode", Toast.LENGTH_SHORT).show();
        // Toast.makeText(mContext, "Sending Last Location..", Toast.LENGTH_SHORT).show();
        //ask for permissions
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(mContext, "Sms Permissions Are Not Available", Toast.LENGTH_SHORT).show();
        }
        Task<Location> LocationTask = fusedLocationProviderClient.getLastLocation();
        LocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    OFFLINE_MESSAGE = OFFLINE_DANGER_CODE + "{[" + level + "," + location.getLatitude() + "," + location.getLongitude() + "]}";
                    smssender(primaryphnno, bnum2, bnum3, OFFLINE_MESSAGE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Toast.makeText(mContext, "Last Location Not Available", Toast.LENGTH_SHORT).show();
                OFFLINE_MESSAGE = OFFLINE_DANGER_CODE + "{[" + level + "," + "335345.45" + "," + "32424.32" + "]}";
                smssender(primaryphnno, bnum2, bnum3, OFFLINE_MESSAGE);
            }
        });

        ///calls When net off

//            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse("tel:" + primaryphnno));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
//            startActivity(intent);
    }


    //https://www.digitalocean.com/community/tutorials/android-location-api-tracking-gps
//current location check and location track service activator // online mode
    void deviceStatus() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
//Not the best practices to get runtime permissions, but still here I ask permissions.
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
//Instantiating the Location request and setting the priority and the interval I need to update the location.
        locationRequest = locationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

//instantiating the LocationCallBack
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    if (locationResult == null) {
                        return;
                    }
                    //Showing the latitude, longitude and accuracy on the home screen.
                    for (Location location : locationResult.getLocations()) {
                        double lat =  location.getLatitude();
                        double lng =  location.getLongitude();
                        int BatteryPercentage=0 ;
                        tools t1 = new tools();
                       // Toast.makeText(mContext, Double.toString(lat) + Double.toString(lng), Toast.LENGTH_SHORT).show();
                        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));
                        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
                        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        // Add the hash and the lat/lng to the document. We will use the hash
                        // for queries and the lat/lng for distance comparisons.
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("geohash", hash);
                        updates.put("lat", lat);
                        updates.put("lng", lng);
                        updates.put("accuracy", location.getAccuracy());
                        updates.put("date", t1.getCurrentDate());
                        updates.put("time", t1.getCurrentTime());
                        updates.put("Device Speed", location.getSpeed());
                        updates.put("BatteryStatus", level);
                        db.collection("Users").document(firebaseAuth.getInstance().getUid()).update(updates);
                        //Toast.makeText(mContext, "Updating Location..", Toast.LENGTH_SHORT).show();
                        dangerStatus();
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }



    //its uploads data to firebase database!! for Danger status by sending random values
    void dangerStatus()
    {
         double garbage  = Math.random();
        // Write a message to the database
        Map<String, Double> m1 = new HashMap<>();
        m1.put("DANGERMODE",garbage);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("UsersStatusData").child(currentuserid());
        myRef.setValue(m1);
    }

String currentuserid()
{
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
        authuserid = user.getUid();
    }
    return authuserid;
}

    @Override
    public void onNetworkChange(boolean isConnected) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(mContext, "Service Destroyed", Toast.LENGTH_SHORT).show();

    }
}