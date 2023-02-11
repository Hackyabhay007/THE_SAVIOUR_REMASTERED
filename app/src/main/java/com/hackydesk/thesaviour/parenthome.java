package com.hackydesk.thesaviour;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import components.Loader;
import components.functiontools;

public class parenthome<permissionsStr> extends AppCompatActivity implements OnMapReadyCallback, connectionchecker.ReceiverListener {


    ArrayList<String> permissionsList;
    String[] permissionsStr = {Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    int permissionsCount = 0;
    ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            permissionsCount = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                    permissionsList.add(permissionsStr[i]);
                                } else if (!hasPermission(parenthome.this, permissionsStr[i])) {
                                    permissionsCount++;
                                }
                            }
                            if (permissionsList.size() > 0) {
                                //Some permissions are denied and can be asked again.
                                askForPermissions(permissionsList);
                            } else if (permissionsCount > 0) {
                                //Show alert dialog
                                showPermissionDialog();
                                askForPermissions(permissionsList);
                            } else {
                                //All permissions granted. Do your stuff 🤞
                                //  addresstext.setText("All permissions are granted!");
                                //   Toast.makeText(mContext, "All Permissions Granted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 340;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;
    TextView childname1;
    String child1userid;
    CardView devicestatus, deviceinfobox;
    SharedPreferences.Editor editor;
    Context mContext;
    Button childbtn1;
    float distance;
    CardView Ringphone;
    CardView alertbox;
    TextView alerttext;
    CardView CallBox;
    CardView settings;
    LocationRequest locationRequest;
    CardView getdirectionlink;
    CardView addressview;
    TextView addresstext;
    CardView addressbtn;
    TextView ringphone;
    TextView callbtnText;
    TextView userstatus, devicestatusText,Distance_alert;
    TextView sharingJouneystatus;
    TextView alert_date, alert_time, alert_accuracy, alert_speed, alert_battery, addressBtnText;
    AlertDialog alertDialog;
    double lat, lng;
    int Ticktock = 0;
    int SosTicktock = 0;
    int SharingJourneyTickTock = 0;
    private boolean permissionboolean = false;
    //it should be blank
    String defaultalertforalertbox = "";
    String child1phonenumber;
    String Child1name;
    String garbageString;
    String RING_CODE = "$$RING$$???";
    private GoogleMap Mymap;
    DocumentReference dbfetcher;
    Loader loader;
    Marker customMarker;
    functiontools functiontools;
    boolean sosStatus = false;
    boolean Sharingjourneystatus = false;
    FirebaseFirestore db;
    FusedLocationProviderClient fusedLocationProviderClient;
    Loader customLoader;
    double latitude ,longitude;
    LinearLayout UserStatusLayout, sharingJourneyBox;
    final private int REQ_CODE_FOR_MICROPHONE = 2789;
    final private int REQ_CODE_FOR_LOCATION_COARSE = 2790;
    final private int REQ_CODE_FOR_LOCATION_FINE = 2799;
    final private int REQ_CODE_FOR_SMS_SEND = 2791;
    final private int REQ_CODE_FOR_SMS_RECEIVE = 2710;
    final private int REQ_CODE_FOR_CALL = 2792;
    final private int REQ_CODE_FOR_CAMERA = 2793;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parenthome);

        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        //ask for permissions
        askForPermissions(permissionsList);
        // checkPermissionsDialog();
        functiontools = new functiontools(parenthome.this, getApplicationContext());
        mContext = parenthome.this;
        byte[] array = new byte[15]; // length is bounded by 7
        new Random().nextBytes(array);
        garbageString = new String(array, StandardCharsets.UTF_8);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(parenthome.this);
        loader = new Loader(parenthome.this);
        devicestatus = findViewById(R.id.deviceStatus);
        deviceinfobox = findViewById(R.id.DeviceStatus);
        childname1 = findViewById(R.id.CHILDNAME1);
        childbtn1 = findViewById(R.id.childbtn1);
        alertbox = findViewById(R.id.alertbox);
        alerttext = findViewById(R.id.alerttext);
        CallBox = findViewById(R.id.callbox);
        Ringphone = findViewById(R.id.RingPhone);
        ringphone = findViewById(R.id.RingPhone1);
        callbtnText = findViewById(R.id.calltext);
        settings = findViewById(R.id.parent_settings);
        userstatus = findViewById(R.id.UserStatus);
        getdirectionlink = findViewById(R.id.getDirectionLink);
        devicestatusText = findViewById(R.id.devicestatustxt);
        alert_date = findViewById(R.id.data_fetchdate);
        alert_time = findViewById(R.id.data_fetch_time);
        alert_battery = findViewById(R.id.data_percentage);
        alert_speed = findViewById(R.id.alert_speed);
        UserStatusLayout = findViewById(R.id.userstatusLayout);
        sharingJourneyBox = findViewById(R.id.sharingjourneybox);
        sharingJouneystatus = findViewById(R.id.SharingJourneyStatus);
        addressbtn = findViewById(R.id.addressButton);
        addresstext = findViewById(R.id.addressbar);
        addressview = findViewById(R.id.addressView);
        addressBtnText = findViewById(R.id.addressbtntext);
        Distance_alert = findViewById(R.id.alert_distance);

        //sharedprefs
        sharedPreferences = parenthome.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //global strings
        child1userid = sharedPreferences.getString("Parentchild1UserId", "");
        child1phonenumber = sharedPreferences.getString("Parentchild1_Phonenumber", child1phonenumber);
        Child1name = sharedPreferences.getString("Parentchild1_Name", "null");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        dbfetcher = db.collection("Users").document(child1userid);

        customLoader = new Loader(parenthome.this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //start loading screen
        customLoader.startLoader();
        //check any one in danger
        alertcheckfrombroadcast();
        //inflates the layout
        layoutinflator();
        //get updates of sos of user
        getSosUpdates(child1userid);
        //get journey updates
        sharingJourneyStatus();
        //asks for gps permission
        functiontools.requestGPSPermission();



        CallBox.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + child1phonenumber));
                callbtnText.setText("Calling....");
                CallBox.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        CallBox.setEnabled(true);
                        callbtnText.setText("Call User");
                    }
                }, 3000);
                startActivity(callIntent);
            }
        });

        Ringphone.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                sendSMSMessage();
                Ringphone.setEnabled(false);
                ringphone.setText("Ringing....");

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        Ringphone.setEnabled(true);
                        ringphone.setText("Ring Again");
                    }
                }, 15000);// set time as per your requirement

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), parent_settings.class);
                startActivity(intent);
            }
        });

        getdirectionlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (functiontools.checkConnection()) {
                    directonlinkgenerator();
                    loader.startLoader("Google Maps Direction", "Getting Accurate Location");
                } else {
                    Toast.makeText(mContext, "Turn On Internet To See Directions", Toast.LENGTH_SHORT).show();
                }
            }
        });

        devicestatus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (deviceinfobox.getVisibility() == View.VISIBLE) {
                    deviceinfobox.setVisibility(View.INVISIBLE);
                    devicestatusText.setText("Device Status");
                } else {
                    deviceinfobox.setVisibility(View.VISIBLE);
                    addressview.setVisibility(View.INVISIBLE);
                    devicestatusText.setText("Close");
                }
            }
        });

        addressbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addressview.getVisibility() == View.VISIBLE) {
                    addressview.setVisibility(View.INVISIBLE);
                    addressBtnText.setText("Address");
                } else {
                    addressview.setVisibility(View.VISIBLE);
                    deviceinfobox.setVisibility(View.INVISIBLE);
                    addressBtnText.setText("Hide");
                }
            }
        });
    }

    //make alert box animated
    void alertcheckfrombroadcast() {
        if (Objects.equals(sharedPreferences.getString("alertforparentactivity", "blank"), "19155")) {
            editor.putString("alertforparentactivity", defaultalertforalertbox);
            //  Toast.makeText(mContext, sharedPreferences.getString("parentchild1", "Your Child") + "Your Child Is Not Safe Opent Activity To Watch Where He is ", Toast.LENGTH_SHORT).show();
            editor.apply();
            alertUiinflator();
        }
    }

    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }

    void alertUiinflator() {
        //alertbox.setVisibility(View.VISIBLE);
        childbtn1.setText("Not Safe");
        childbtn1.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.red));
    }

    void layoutinflator() {
        childname1.setText(sharedPreferences.getString("Parentchild1_Name", "null"));
    }

    void sendSMSMessage() {

        if (hasPermission(getApplicationContext(), Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+91" + child1phonenumber, null, garbageString + " " + child1userid + RING_CODE, null, null);
        } else {
            askForPermissions(permissionsList);
        }

    }

    //its loads map and reloads map
    void maploader(double lat, double lng) {
//        Mymap.getUiSettings().setZoomGesturesEnabled(true);
//        Mymap.getUiSettings().setRotateGesturesEnabled(true);
        LatLng position = new LatLng(lat, lng);
        //Mymap.addMarker(new MarkerOptions().position(position).title("Location Fetched"));

        Marker m1 = Mymap.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title("User Position")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_region_64__1_)));
        Mymap.moveCamera(CameraUpdateFactory.newLatLng(position));
        Mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f));

        Mymap.moveCamera(CameraUpdateFactory.newLatLng(position));
        Mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f));
        Mymap.addCircle(new CircleOptions().center(position).fillColor(Color.argb(41, 41, 0, 210)).strokeColor(Color.BLACK).radius(10).strokeWidth(1));
    }

    //its loads map and reloads map
    void mapUpdater(double lat, double lng) {
        Mymap.getUiSettings().setZoomGesturesEnabled(true);
        Mymap.getUiSettings().setRotateGesturesEnabled(true);
        LatLng position = new LatLng(lat, lng);
        //Mymap.addMarker(new MarkerOptions().position(position).title("Location Fetched"));

        Mymap.clear();
        customMarker = Mymap.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title("User Position"));

        if (sosStatus) {
            customMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_region_64));
        } else {
            customMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_region_64__1_));
        }

        customMarker.setPosition(position);

        // Mymap.moveCamera(CameraUpdateFactory.newLatLng(position));
        Mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f));

    }

    //this methods syncs from Database and refreshes in real time
    void LiveListener() {
        dbfetcher.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Locationstatus();
                    customLoader.dismissLoader();
                    Log.d("TAG", "Current data: " + snapshot.getData());
                    lat = Double.parseDouble(String.valueOf(snapshot.get("lat")));
                    lng = Double.parseDouble(String.valueOf(snapshot.get("lng")));
                    String Alert_phn = child1phonenumber;
                    String Alert_battery = String.valueOf(snapshot.get("BatteryStatus"));
                    String Alert_speed = String.valueOf(snapshot.get("Device Speed"));
                    String Alert_accuracy = String.valueOf(snapshot.get("accuracy"));
                    String date = String.valueOf(snapshot.get("date"));
                    String time = String.valueOf(snapshot.get("time"));
                    latitude = lat;
                    longitude = lng;
                    double speed = Double.parseDouble(Alert_speed);
                    speed = Math.round(speed);
                    speed = ((speed * 3600) / 1000);

                    Alert_speed = String.valueOf(speed);
                    alert_time.setText(time.toUpperCase());
                    alert_date.setText(date);
                    alert_speed.setText(Alert_speed + "Kmph");
                    alert_battery.setText(Alert_battery + "%");
                    Mymap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(Child1name)
                            .snippet("Position Fetched"));
                    //calculate distance
                    distanceCalculator();
                    mapUpdater(lat, lng);
                    getCompleteAddressString(lat, lng);

                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });


    }

        @SuppressLint("SetTextI18n")
        void distanceCalculator() {

           if(functiontools.checkGpsStatus())
           {
               float distancefrommylocation = functiontools.distanceCalculator(latitude, longitude);
               @SuppressLint("DefaultLocale") String formattedString = String.format("%.02f", distancefrommylocation);
               if (distancefrommylocation < 999) {
                   Distance_alert.setText(formattedString + " Metres");
               } else {
                   Distance_alert.setText(formattedString + "Km");
               }
           }

           else {
               Toast.makeText(mContext, "Turn on Gps To get Distance", Toast.LENGTH_SHORT).show();
               functiontools.requestGPSPermission();
           }
        }

    boolean checkGpsAndInternetpermissions() {


        if (functiontools.checkGpsStatus()) {
            if (functiontools.checkConnection()) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(parenthome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                } else {
                    permissionboolean = true;
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No Internet");
                builder.setMessage("Turn On Internet")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Grant permission
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } else {
            functiontools.requestGPSPermission();
        }
        return permissionboolean;
    }

    void Locationstatus() {
        Ticktock++;
        if (Ticktock > 1) {
            userstatus.setText("Showing Live Location");
            userstatus.setTextColor(Color.BLACK);
            UserStatusLayout.setBackgroundColor(Color.GREEN);
            //  userstatus.setBackgroundColor(Color.GREEN);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Mymap = googleMap;
        Mymap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.mapdarkmodestyle));
        LiveListener();

    }


    boolean trigger = true;

    void directonlinkgenerator() {

                        dbfetcher = db.collection("Users").document(child1userid);
                        dbfetcher.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    double Childlat = Double.parseDouble(String.valueOf(documentSnapshot.get("lat")));
                                    String Childlng = String.valueOf(documentSnapshot.get("lng"));
                                    loader.dismissloader("Google Maps Direction", "Getting Accurate Location");

                                    Toast.makeText(getApplicationContext(), "Opening Direction to Last Location", Toast.LENGTH_SHORT).show();
                                    getDirections(Childlat, Double.parseDouble(Childlng));

                                } else {
                                    Toast.makeText(getParent(), "Cant find Directions Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(parenthome.this, "Last Location Not Found", Toast.LENGTH_SHORT).show();
                            }
                        });

    }

    public void getDirections(double lat, double lon) {
     functiontools.getdirections(lat,lng);
    }

    void getSosUpdates(String child1userid)
    {

        //gets status of child
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database.getReference("UsersStatusData").child(child1userid).child("DANGERMODE");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SosTicktock++;

                if (SosTicktock>1)
                {
                    sosStatus =true;
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                    r.play();
                    alertUiinflator();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef2.addValueEventListener(postListener);
    }

    void sharingJourneyStatus()
    {
        //gets status of child
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database.getReference("UsersStatusData").child(child1userid).child("SHARINGLOCATION");

        ValueEventListener postListener = new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForColorStateLists"})
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SharingJourneyTickTock++;

                if (SharingJourneyTickTock>1)
                {
                    sharingJouneystatus.setVisibility(View.VISIBLE);
                    Sharingjourneystatus=true;
                    sharingJouneystatus.setText("Sharing Journey");
                   /// sharingJouneystatus.setTextColor(Color.BLACK);
                    sharingJourneyBox.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.hardtext));
                  //  Toast.makeText(mContext, "Updatinggg", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef2.addValueEventListener(postListener);
    }



    String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current  address", strReturnedAddress.toString());
                addresstext.setText(strReturnedAddress.toString());
            } else {
                Log.w("My Current  address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current  address", "Canont get Address!");
        }
        return strAdd;
    }


    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }


    private void showPermissionDialog() {
        //addresstext.setText("Showing settings dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog_Alert_Bridge);
        builder.setTitle("Permission required")
                .setMessage("Some permissions are needed to be allowed to use this app without any problems.").setCancelable(false)
                .setPositiveButton("Allow", (ans,yes) -> {
                    permissionsList = new ArrayList<>();
                    permissionsList.addAll(Arrays.asList(permissionsStr));
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);

                });
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        }
    }

    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
          //  Toast.makeText(this, "Checking Permissions", Toast.LENGTH_SHORT).show();
            permissionsLauncher.launch(newPermissionStr);

        } else {
            showPermissionDialog();

        }
    }








    @Override
    public void onNetworkChange(boolean isConnected) {

    }
}

