package com.hackydesk.thesaviour;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import components.functiontools;

public class homie extends AppCompatActivity {
    FirebaseFirestore db;
    //dont remove it it will affect primarygaurds and firestore database
    FirebaseFirestore db2 = FirebaseFirestore.getInstance();
    DocumentReference dbfetcher;
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest = new LocationRequest();
    LocationCallback locationCallback;
    BottomNavigationView bottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homie);
        FirebaseMessaging.getInstance().subscribeToTopic("thesaviours")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("TAG", msg);
                        //  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

        db = FirebaseFirestore.getInstance();
        dbfetcher = db.collection("Users").document(FirebaseAuth.getInstance().getUid());
        sharedPreferences = this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //this functions loads data to local from server
        DataLoadtoLocalDbFromServer();


        String[] permission = {Manifest.permission.RECEIVE_SMS};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, 239);
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //creating objects of fragments
        Fragment safehome = new safehome();
        Fragment communityfragment = new communityfragment();
        Fragment settings = new settings();
        Fragment emergencydailer = new emergencydailer();
        Fragment bodygaurds = new bodyguards();
        fragloader(safehome, 0);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // do stuff
            switch (item.getItemId()) {
                case R.id.nav_home:
                    fragloader(safehome, 1);
                    return true;

                case R.id.nav_maps:
                    fragloader(communityfragment, 1);
                    return true;

                case R.id.nav_settings:
                    fragloader(settings, 1);
                    return true;

                case R.id.nav_dailer1:
                    fragloader(emergencydailer, 1);
                    return true;
                case R.id.nav_gaurds:
                    fragloader(bodygaurds, 0);
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

     void DataLoadtoLocalDbFromServer(){

        functiontools extra = new functiontools(homie.this,getApplicationContext());
         extra.dataSeparatorAndLoadToLocal();
     }



    @Override
    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce=false;
//            }
//        }, 2000);
    }



}