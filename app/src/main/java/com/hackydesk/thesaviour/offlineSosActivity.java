package com.hackydesk.thesaviour;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import components.Loader;
import components.functiontools;

public class offlineSosActivity extends AppCompatActivity implements OnMapReadyCallback {


    GoogleMap Mymap;
    Loader loader;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    TextView AddressBar,ChildName,Time,Date,BatteryPercentage,Direction,RingPhone,DistanceFrommyLocation;
    Button Showdetailsbtn;
    SharedPreferences sharedPreferences;
    String child1name,child1battery,child1lngtime,child1lngdate;
    LinearLayout DetailsContainer;

    Double child1lat,child1lng;
    String RING_CODE = "$$RING$$???";
    functiontools extraFeatures;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_sos);

        sharedPreferences = offlineSosActivity.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        loader =new Loader(offlineSosActivity.this);
        AddressBar = findViewById(R.id.addressbar);
        ChildName = findViewById(R.id.CHILDNAME1);
        Time = findViewById(R.id.data_fetch_time);
        Date = findViewById(R.id.data_fetchdate);
        BatteryPercentage = findViewById(R.id.data_percentage);
        Direction =findViewById(R.id.alert_speed);
        RingPhone = findViewById(R.id.Ringphn);
        Showdetailsbtn = findViewById(R.id.showdetailsbtn);
        DistanceFrommyLocation = findViewById(R.id.Distanceofuser);
        DetailsContainer = findViewById(R.id.detailsContainer);


        extraFeatures = new functiontools(offlineSosActivity.this,getApplicationContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //request gps permission
        extraFeatures.requestGPSPermission();

        Direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkData())
                {
                    loader.startLoader();
                    directonlinkgenerator(child1lat,child1lng);
                }
            }
        });

        RingPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage();
                RingPhone.setEnabled(false);
                RingPhone.setText("Ringing");

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        RingPhone.setEnabled(true);
                        RingPhone.setText("Ring Again");
                    }
                },15000);// set time as per your requirement

            }
        });

        Showdetailsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DetailsContainer.getVisibility() == View.VISIBLE)
                {
                    Showdetailsbtn.setText("Hide Details");
                    DetailsContainer.setVisibility(View.INVISIBLE);
                }

                else if(DetailsContainer.getVisibility() == View.INVISIBLE) {
                    Showdetailsbtn.setText("SHOW DETAILS");
                    DetailsContainer.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Mymap = googleMap;
        //it initiate all methods after checking null data
        InitiateAction();
    }


    void sendSMSMessage() {
       String  child1userid = sharedPreferences.getString("Parentchild1UserId", "");
      String  child1phonenumber = sharedPreferences.getString("Parentchild1_Phonenumber", "");
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+91"+child1phonenumber, null,  RING_CODE, null, null);
        } else {
            ActivityCompat.requestPermissions(offlineSosActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    1897);
        }
        }



    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);


    }


    //initaite all methods
    void InitiateAction()
    {
        if (checkData())
        {
            getSosData();
            //inflates ui
            uiInflator();
            //shows ditance on alertbox
            distanceCalculator();
        }
    }


    boolean checkData()
    {
        boolean result =false;
        child1name = sharedPreferences.getString("CHILD1_NAME","null");
        child1battery = sharedPreferences.getString("CHILD1_BATTERY_DATA","null");
        if(!child1name.contains("null") && !child1battery.contains("null"))
        {
            result = true;
        }
        return result;
    }

    //it can get null exception so we have to check if data is null or not
    void getSosData()
    {
        child1name = sharedPreferences.getString("CHILD1_NAME","null");
        child1lat = Double.parseDouble(sharedPreferences.getString("CHILD1_LAT","null"));
        child1lng = Double.valueOf((sharedPreferences.getString("CHILD1_LNG","null")));
       // Toast.makeText(this, child1lat, Toast.LENGTH_SHORT).show();
        child1battery = sharedPreferences.getString("CHILD1_BATTERY_DATA","null");
        child1lngtime =sharedPreferences.getString("CHILD1_SOS_TIME","null");
        child1lngdate=sharedPreferences.getString("CHILD1_SOS_DATE","null");
    }


    void uiInflator()
    {
        maploader(child1lat,child1lng);
        getCompleteAddressString(child1lat,child1lng);
        ChildName.setText(child1name);
        Time.setText(child1lngtime);
        Date.setText(child1lngdate);
        BatteryPercentage.setText(child1battery);
    }

    //its loads map and reloads map
    void maploader(double lat , double lng) {
        Mymap.getUiSettings().setZoomGesturesEnabled(true);
        Mymap.getUiSettings().setRotateGesturesEnabled(true);
        LatLng position = new LatLng(lat, lng);
        Marker m1 =  Mymap.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title("User Position")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_region_64)));
        Mymap.moveCamera(CameraUpdateFactory.newLatLng(position));
        Mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,18f));
        Mymap.moveCamera(CameraUpdateFactory.newLatLng(position));
        Mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,18f));
        Mymap.addCircle(new CircleOptions().center(position).fillColor(Color.argb(41, 41, 0, 210)).strokeColor(Color.BLACK).radius(10).strokeWidth(1));
    }


    void directonlinkgenerator(double glat,double glng)
    {

        extraFeatures.getdirections(glat,glng);
        loader.dismissLoader();
    }

//calculates distance and show it on screen
    @SuppressLint("SetTextI18n")
    void distanceCalculator() {
        functiontools extra = new functiontools(offlineSosActivity.this, getApplicationContext());
        float distancefrommylocation = extra.distanceCalculator(child1lat, child1lng);
        @SuppressLint("DefaultLocale") String formattedString = String.format("%.02f", distancefrommylocation);
        if (distancefrommylocation < 999) {
            DistanceFrommyLocation.setText(formattedString + " Metres");
        } else {
            DistanceFrommyLocation.setText(formattedString + "Km");
        }
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
                AddressBar.setText(strReturnedAddress.toString());
            } else {
                Log.w("My Current  address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current  address", "Canont get Address!");
        }
        return strAdd;
    }


}