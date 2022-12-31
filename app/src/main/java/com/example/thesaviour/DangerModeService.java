package com.example.thesaviour;

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
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
    Integer livelocationrefresh;
    LocationRequest locationRequest;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getBaseContext();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        sharedPreferences = mContext.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        primarybname = sharedPreferences.getString("primarytext", "name");
        primaryphnno = sharedPreferences.getString("primaryphnno", "100");
        bnum2 = sharedPreferences.getString("bnum2", "100");
        bnum3 = sharedPreferences.getString("bnum3", "100");
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Log.e("Service", "Danger Service is running...");

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();
       initiateDangermode();

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        Notification.Builder notification = null;
        Intent SafeHomeIntent = new Intent(this, homie.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, SafeHomeIntent, PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNELID)

                    .setContentText("Initiated All Alerts")
                    .setContentTitle("Danger Mode enabled")
                    .setSmallIcon(R.drawable.gaurd1)
                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.red))
                    .setColorized(true).setColor(Color.RED)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);

        }

        startForeground(1001, notification.build());
        return super.onStartCommand(intent, flags, startId);

    }

    void initiateDangermode()
    {
        Toast.makeText(mContext, "STARTING........", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mContext, "SENDING LOCATION ......", Toast.LENGTH_SHORT).show();
            // Toast.makeText(mContext, "SENDING LIVE AUDIO...", Toast.LENGTH_SHORT).show();
            //calls and sends current co ordinates to server
           smssender(primaryphnno, bnum2, bnum3, "Hello i am in danger please help,its my live location and other details "+"https://hackyabhay007.github.io/The_saviour_admin/??"+currentuserid());
            currentlocation();
        } else {
            //calls when net off
            Toast.makeText(mContext, "INTERNET OFF CALLING..", Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "Sending Last Location..", Toast.LENGTH_SHORT).show();
            //sendig lastlocation link if offline
            String[] linkoflastlocation = new String[1];
            String name[] = new String[2];
            //asl for permissions
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //permissions should be added or call by any function
            }
            Task<Location> LocationTask = fusedLocationProviderClient.getLastLocation();
            LocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        linkoflastlocation[0] = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude() ;
                        //calling sms sender from here because
                       smssender(primaryphnno, bnum2, bnum3, "Hello i am the saviour App user i am in danger its my last location" + linkoflastlocation[0]);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@androidx.annotation.NonNull Exception e) {
                    Toast.makeText(mContext, "Last Location Not Avaliable", Toast.LENGTH_SHORT).show();
                }
            });

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:+91" + primaryphnno));
            //startActivity(intent);
        }
    }

    void smssender(String primaryphnno, String bnum2, String bnum3, String Message) {

        final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
//            ActivityCompat.requestPermissions((Activity) getBaseContext(),
//                    new String[]{Manifest.permission.SEND_SMS},
//                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Set the destination phone number to the string in editText.
            String destinationAddress1 = primaryphnno;
            String destinationAddress2 = bnum2;
            String destinationAddress3 = bnum3;
            // Get the text of the SMS message.
            String smsMessage = Message;
            // Set the service center address if needed, otherwise null.
            String scAddress = null;
            // Set pending intents to broadcast
            // when message sent and when delivered, or set to null.
            PendingIntent sentIntent = null, deliveryIntent = null;
            // Use SmsManager.
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage
                    (destinationAddress1, scAddress, smsMessage,
                            sentIntent, deliveryIntent);
            Toast.makeText(mContext, "SENDING MESSAGE TO : " + destinationAddress1, Toast.LENGTH_SHORT).show();
            smsManager.sendTextMessage
                    (destinationAddress2, scAddress, smsMessage,
                            sentIntent, deliveryIntent);
            Toast.makeText(mContext, "SENDING MESSAGE TO : " + destinationAddress2, Toast.LENGTH_SHORT).show();
            smsManager.sendTextMessage
                    (destinationAddress3, scAddress, smsMessage,
                            sentIntent, deliveryIntent);
            Toast.makeText(mContext, "SENDING MESSAGE TO : " + destinationAddress3, Toast.LENGTH_SHORT).show();
        }
    }

    //https://www.digitalocean.com/community/tutorials/android-location-api-tracking-gps
//current location check and location track service activator
    void currentlocation() {

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
                       // Toast.makeText(mContext, Double.toString(lat) + Double.toString(lng), Toast.LENGTH_SHORT).show();
                        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));
                        // Add the hash and the lat/lng to the document. We will use the hash
                        // for queries and the lat/lng for distance comparisons.
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("geohash", hash);
                        updates.put("lat", lat);
                        updates.put("lng", lng);
                        updates.put("accuracy", location.getAccuracy());
                        updates.put("Device Speed", location.getSpeed());
                        db.collection("Users").document(firebaseAuth.getInstance().getUid()).update(updates);
                        Toast.makeText(mContext, "Updating Location..", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

String currentuserid()
{
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
        // Name, email address, and profile photo Url
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
//            // Check if user's email is verified
//            boolean emailVerified = user.isEmailVerified();
        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getIdToken() instead.
        authuserid = user.getUid();

    }
    return authuserid;
}

    @Override
    public void onNetworkChange(boolean isConnected) {

    }



}