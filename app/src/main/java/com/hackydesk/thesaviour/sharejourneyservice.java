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
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import components.tools;

public class sharejourneyservice extends Service {

   private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
   private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    NotificationManager notificationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Context mContext;
    boolean trigger = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        deviceStatus();
        mContext = getBaseContext();
        notificationManager = getSystemService(NotificationManager.class);

        final String CHANNELID = "345356";
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
                    .setContentText("Sharing live Location With Guardians")
                    .setContentTitle("The Saviour")
                    .setSmallIcon(R.drawable.mainlog_transparent_bg)
                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.bigbtnborder))
                    .setColorized(true).setColor(Color.RED)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);
        }

        startForeground(Integer.parseInt(CHANNELID), notification.build());

        return START_NOT_STICKY;
        // return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void deviceStatus() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//Not the best practices to get runtime permissions, but still here I ask permissions.
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
//Instantiating the Location request and setting the priority and the interval I need to update the location.
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       // locationRequest.setSmallestDisplacement(10);
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
                        // Toast.makeText(getApplicationContext(), Double.toString(lat) + Double.toString(lng), Toast.LENGTH_SHORT).show();
                        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));
                        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus =getApplicationContext().registerReceiver(null, ifilter);
                        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        // Add the hash and the lat/lng to the document. We will use the hash
                        // for queries and the lat/lng for distance comparisons.
                        tools t1 = new tools();
                        Map<String, Object> updates = new HashMap<>();
                        //it saves the coordiante of first coordinates then update further
                        if(trigger)
                        {
                            updates.put("startjourneylat",lat);
                            updates.put("startjourneylng",lng);
                            trigger=false;
                        }
                        else {
                            updates.put("lat", lat);
                            updates.put("lng", lng);
                        }
                        updates.put("date", t1.getCurrentDate());
                        updates.put("time", t1.getCurrentTime());
                        updates.put("geohash", hash);
                        updates.put("accuracy", location.getAccuracy());
                        updates.put("Device Speed", location.getSpeed());
                        updates.put("BatteryStatus", level);
                        db.collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).update(updates);
                      //  Toast.makeText(getApplicationContext(), "Sharing Live Location...", Toast.LENGTH_SHORT).show();
                        journeyStatus();
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    void journeyStatus()
    {
        tools t1 = new tools();
        double garbage  = Math.random();
        // Write a message to the database
        Map<String, Double> m1 = new HashMap<>();
        m1.put("SHARINGLOCATION",garbage);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("UsersStatusData").child(t1.currentUserid());
        myRef.setValue(m1);
    }




}