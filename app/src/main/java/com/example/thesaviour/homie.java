package com.example.thesaviour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.thesaviour.safehome;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class homie extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest = new LocationRequest();
    LocationCallback locationCallback;

    BottomNavigationView bottomNavigationView;
    int count = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            count++;
            if (count == 5) {
                count = 0;
                Toast.makeText(this, "STARTING........", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "INITIATING CALL........", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "SENDING LOCATION ......", Toast.LENGTH_SHORT).show();
                return true;
            } else
                return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homie);
        String [] permission ={Manifest.permission.RECEIVE_SMS};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission,239);
        }


        bottomNavigationView = findViewById(R.id.bottom_navigation);
//creating objects of fragments
        Fragment safehome =new safehome();
        Fragment communityfragment= new communityfragment();
        Fragment settings= new settings();
        Fragment emergencydailer= new emergencydailer();
        Fragment bodygaurds= new bodygaurds();
        fragloader(safehome,0);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // do stuff
            switch (item.getItemId()) {
                case R.id.nav_home:
                    fragloader(safehome,1);
                    return true;

                case R.id.nav_maps:
                    fragloader(communityfragment,1);
                    return true;

                case R.id.nav_settings:

                    fragloader(settings,1);
                    return true;

                case R.id.nav_dailer1:
                    fragloader(emergencydailer,1);
                    return true;
                case R.id.nav_gaurds:
                    fragloader(bodygaurds,0);
                    return true;
            }
            return false;
        });

    }




    // method to add fragment or replace fragnment
        void  fragloader(Fragment fragment , int flag)
    {
        FragmentManager fm =getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if(flag==0)
            ft.replace(R.id.fragment_container_view,fragment).commit();
        else
            ft.replace(R.id.fragment_container_view,fragment).commit();
    }




}