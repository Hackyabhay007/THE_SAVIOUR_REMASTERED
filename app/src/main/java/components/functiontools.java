package components;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hackydesk.thesaviour.FcmNotificationsSender;
import com.hackydesk.thesaviour.R;
import com.hackydesk.thesaviour.connectionchecker;
import com.hackydesk.thesaviour.firsttime;
import com.hackydesk.thesaviour.userguide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class functiontools implements connectionchecker.ReceiverListener {
    Context context;
    Activity activity;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    double latitude, longitude;
    ArrayList<String> parenttokens = new ArrayList<>();
    ArrayList<String> parentdata = new ArrayList<>();
    int totalguardians = 0;


    public functiontools(Context Mycontext) {
        context = Mycontext;
    }

    functiontools(Activity Myactivity) {
        activity = Myactivity;
    }

    public functiontools(Activity Myactivity, Context Mycontext) {
        activity = Myactivity;
        context = Mycontext;
    }

    public boolean checkConnection() {
        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();
        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
        // Initialize listener
        connectionchecker.Listener = this;
        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        //sending what to do when net  on
        return isConnected;
    }

    public boolean checkGpsStatus() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void requestGPSPermission() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder();

        locationSettingsRequestBuilder.addLocationRequest(locationRequest);
        locationSettingsRequestBuilder.setAlwaysShow(true);

        SettingsClient settingsClient = LocationServices.getSettingsClient(activity.getApplicationContext());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //  Toast.makeText(activity.getApplicationContext(), "Gps is On", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  Toast.makeText(activity.getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();

                try {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    resolvableApiException.startResolutionForResult(activity,
                            234353);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

//used to send notificattions through firebase messaging


    ////start of counter
    public void totalGuardiansCounter(CardView c1 , Button b1) {
        tools t1 = new tools();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cities = db.collection("ParentsData");
        db = FirebaseFirestore.getInstance();
        db.collection("ParentsData")
                .whereEqualTo("childid", t1.currentUserid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            totalguardians = task.getResult().size();
                            if (totalguardians == 0) {
                                c1.setVisibility(View.INVISIBLE);
                                b1.setVisibility(View.INVISIBLE);
                                AlertDialog.Builder builder = new AlertDialog.Builder((context));
                                builder.setTitle("No Guardians Linked Yet");
                                builder.setMessage("Sos And Sharing Journey Feature will not  work until Installation of The Saviour App in Guardian phone , Share Guardian Code Available In Your Profile Section With Guardian By Which Login Procedure Can be Done ");
                                builder.setIcon(R.drawable.mainlog_transparent_bg);
                                builder.setPositiveButton("Link Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(activity, userguide.class);
                                        activity.startActivity(intent);
                                    }
                                }).setNegativeButton("OK ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                builder.show();
                            }
                            // Toast.makeText(context, String.valueOf(totalguardians), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    @SuppressLint("SuspiciousIndentation")
    public void sendOnlineMesageToparent(String title, String message) {
        tools t1 = new tools();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cities = db.collection("ParentsData");
        db = FirebaseFirestore.getInstance();
        db.collection("ParentsData")
                .whereEqualTo("childid", t1.currentUserid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAGghj", document.getId() + " => " + document.getData());

                                if (document.exists()) {
                                    // Get data from DocumentSnapshot
                                    String data = document.getString("token");
                                    parenttokens.add(data);


                                }
                            }

                            for (String tokendata : parenttokens) {
                                new FcmNotificationsSender(tokendata, title, message, context).SendNotifications();
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

//


    public float distanceCalculator(double lat, double lng) {
        float DISTANCE = 0;
        //Create a location based on the input coordinates
        Location destination = new Location("destination");
        destination.setLatitude(lat);
        destination.setLongitude(lng);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //Get the current device location
        LocationManager locationManager = null;
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(currentLocation==null)
        {
            DISTANCE = 0;
        }
        else {
            DISTANCE = currentLocation.distanceTo(destination);
        }

        return DISTANCE;
    }

    public void getdirections(double lat, double lng) {
        //Create a Uri for passing to the intent
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        //Start the intent to get directions
        activity.startActivity(mapIntent);
    }

    //it sents to the page where autostart permission is available
    public void repairDevice() {
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            activity.startActivity(intent);
        }
    }

    //check dnd mode is or not
    public boolean isDndModeOn() {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audio.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

    public void onNetworkChange(boolean isConnected) {

    }


    //get currentlocation
    ArrayList<Double> latlong = new ArrayList<>();

    public ArrayList<Double> deviceAccurateLocation() {


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
//Not the best practices to get runtime permissions, but still here I ask permissions.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
//Instantiating the Location request and setting the priority and the interval I need to update the location.
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

//instantiating the LocationCallBack
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                //Showing the latitude, longitude and accuracy on the home screen.
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    latlong.add(0, latitude);
                    latlong.add(1, longitude);
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        return latlong;
    }

    FirebaseFirestore db;
    DocumentReference dbfetcher;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public void dataSeparatorAndLoadToLocal() {
        db = FirebaseFirestore.getInstance();
        dbfetcher = db.collection("Users").document(FirebaseAuth.getInstance().getUid());
        sharedPreferences = activity.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        dbfetcher.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String bodygaurds = (String) documentSnapshot.get("bodyguards");

                            //fetch data for user profile and loads to local server
                            String name = (String) documentSnapshot.get("fname");
                            String email = (String) documentSnapshot.get("email");
                            String phonenumber = (String) documentSnapshot.get("number");

                            editor.putString("profile_name", name);
                            editor.putString("profile_email", email);
                            editor.putString("profile_phnnumber", phonenumber);
                            editor.apply();
                            dataseperator(bodygaurds);
                            //Toast.makeText(getContext(), "Bodygaurds Fetched ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Something Wrong Happened", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity, firsttime.class);
                            editor.clear();
                            editor.apply();
                            FirebaseAuth.getInstance().signOut();
                            activity.startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void dataseperator(String bodygaurds) {

        String[] bdata = bodygaurds.split("[,]", 0);
        int datalength = bdata.length;
        if (datalength == 9) {
            String bname1 = bdata[0];
            String bnum1 = bdata[1];
            Boolean b1 = Boolean.valueOf(bdata[2]);
            String bname2 = bdata[3];
            String bnum2 = bdata[4];
            Boolean b2 = Boolean.valueOf(bdata[5]);
            String bname3 = bdata[6];
            String bnum3 = bdata[7];
            Boolean b3 = Boolean.valueOf(bdata[8]);
            //adding data to database for other use
            if (b1) {
                editor.putString("primarytext", bname1);
                editor.putString("primaryphnno", bnum1);
            }
            if (b2) {
                editor.putString("primarytext", bname2);
                editor.putString("primaryphnno", bnum2);
            }
            if (b3) {
                editor.putString("primarytext", bname3);
                editor.putString("primaryphnno", bnum2);
            }
            editor.putString("bname1", bname1);
            editor.putString("bname2", bname2);
            editor.putString("bname3", bname3);
            editor.putString("bnum1", bnum1);
            editor.putString("bnum2", bnum2);
            editor.putString("bnum3", bnum3);
            editor.putBoolean("b1", b1);
            editor.putBoolean("b2", b2);
            editor.putBoolean("b3", b3);
            editor.apply();
        } else {
            Toast.makeText(context, "DATA NOT FOUND", Toast.LENGTH_SHORT).show();
        }
    }

    boolean trigger = true;
  public void syncDeviceStatus() {
      db = FirebaseFirestore.getInstance();
      dbfetcher = db.collection("Users").document(FirebaseAuth.getInstance().getUid());
        Loader loader = new Loader(activity);

        loader.startLoader("Sync Data","Syncing Data With Server");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
//Not the best practices to get runtime permissions, but still here I ask permissions.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions( activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
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
                        Intent batteryStatus = context.registerReceiver(null, ifilter);
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
                     if (trigger)
                     {
                         db.collection("Users").document(firebaseAuth.getInstance().getUid()).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 functiontools syncnotification = new functiontools(activity,context);
                                 SharedPreferences sharedPreferences = context.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
                                 String childname = sharedPreferences.getString("profile_name","User");
                                 sendOnlineMesageToparent("The Saviour",childname+" Data Synced With Server Check Whats The Status");
                                 loader.dismissloader("Sync Data","Syncing Data With Server");
                                 Toast.makeText(context, "Data Synced Successfully", Toast.LENGTH_SHORT).show();
                                 trigger = false;

                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 loader.dismissLoader();
                                 Toast.makeText(context, "Data Sync Failed Try Again", Toast.LENGTH_SHORT).show();
                             }
                         });
                     }
                        //Toast.makeText(mContext, "Updating Location..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }
}
