package com.example.thesaviour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Parent_Activity extends AppCompatActivity {
    DocumentReference dbfetcher;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences ;
    String child1userid;
    String GmapsCurrentLocationLink;
    TextView act_name;
    TextView act_number;
    TextView act_accuracy;
    TextView act_percentage;
    TextView act_speed;
    TextView act_mapslink;
    WebView webView;
    TextView BackToHome;
    String Childlat;
    String Childlng;
    String DirectionLink;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Parent_Activity.this);
         webView = (WebView) findViewById(R.id.mapview);
        sharedPreferences = Parent_Activity.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        child1userid = sharedPreferences.getString("Parentchild1UserId", "");
        act_name = findViewById(R.id.alert_name);
        act_number = findViewById(R.id.alert_phn);
        act_accuracy = findViewById(R.id.alert_accuracy);
        act_percentage = findViewById(R.id.alert_percentage);
        act_speed = findViewById(R.id.alert_speed);
        act_mapslink = findViewById(R.id.alert_maps);
        BackToHome = findViewById(R.id.ParentActivityHead);

        //inflates layout for Activity and sets lat long
        MapAlertInflator(child1userid);
        //loads map
        maploader();
        act_mapslink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               DirectionLinkGenerator();
            }
        });

        BackToHome.setText( sharedPreferences.getString("Parentchild1_Name", "") + " Details");
        BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), parenthome.class);
                startActivity(intent);
            }
        });
    }

    void MapAlertInflator(String childuserid)
    {
        dbfetcher = db.collection("Users").document(childuserid);
        dbfetcher.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            String child1Name = (String) documentSnapshot.get("fname");
                            String child1phonenumber = String.valueOf( documentSnapshot.get("number"));
                            String child1Accuracy=  String.valueOf( documentSnapshot.get("accuracy")) ;
                           // String child1Battery = (String) documentSnapshot.get("number");
                            String child1Speed = String.valueOf( documentSnapshot.get("Device Speed"));
                            //using lat lng globally for generating direction link
                             String lat = String.valueOf(documentSnapshot.get("lat"));
                             String lng =String.valueOf(documentSnapshot.get("lng"));
                            String child1MapsLink = "https://maps.google.com/?q=" + lat + "," + lng ;

                            act_name.setText(child1Name);
                            act_number.setText(child1phonenumber);
                            act_accuracy.setText(child1Accuracy);
                            act_percentage.setText("90%");
                            act_speed.setText(child1Speed);
                            GmapsCurrentLocationLink = child1MapsLink;
                        }
                        else {
                            Toast.makeText(getParent(), "User not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getParent(), "Something Went Wrong Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //its loads map and reloads map
    void maploader(){
        int ReloadMapAfter=30000;
        webView.loadUrl("https://hackyabhay007.github.io/TheSaviourMapBox/??"+child1userid);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            private long time = 0;

            @Override
            public void run()
            {
                webView.reload();
                time += 1000;
                Log.d("TimerExample", "Going for... Map Refresh " + time);
                h.postDelayed(this, ReloadMapAfter);
            }
        }, ReloadMapAfter);
    }

    //gets user current location and generates direction Link and Opens in GMaps
    void DirectionLinkGenerator() {

       fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//Not the best practices to get runtime permissions, but still here I ask permissions.
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
//instantiating the LocationCallBack
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            //fetching child location
                            dbfetcher = db.collection("Users").document(child1userid);
                            dbfetcher.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists())
                                    {
                                        Childlat = String.valueOf(documentSnapshot.get("lat"));
                                        Childlng =String.valueOf(documentSnapshot.get("lng"));
                                        Toast.makeText(getApplicationContext(), String.valueOf(Childlat)+"wadaw", Toast.LENGTH_SHORT).show();
                                        double Gaurdlat =  location.getLatitude();
                                        double Gaurdlng =  location.getLongitude();
                                        DirectionLink= "https://www.google.com/maps/dir/?api=1&origin="+String.valueOf(Gaurdlat)+","+ String.valueOf(Gaurdlng)+"&"+"destination="+Childlat+","+Childlng;
                                        MapAlertInflator(child1userid);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                         intent.setData(Uri.parse(DirectionLink));
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(getParent(), "User not Found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Parent_Activity.this, "Last Location Not Found", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

    }

}