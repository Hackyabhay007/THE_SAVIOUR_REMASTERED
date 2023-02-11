package components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;


public class tools {
Activity activity;



    FirebaseFirestore db;
    //dont remove it it will affect primarygaurds and firestore database
    FirebaseFirestore db2 = FirebaseFirestore.getInstance();
    DocumentReference dbfetcher;
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    String currentDate;
    String currentTime;
    FusedLocationProviderClient fusedLocationProviderClient;
    //returns current user id


    public String currentUserid()
    {
        String authuserid="";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            authuserid = user.getUid();
        }
        return authuserid;
    }

    public String getCurrentDate()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat Currentdate = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = Currentdate.format(new Date());
        return  currentDate;
    }

    public String getCurrentTime()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat Currenttime = new SimpleDateFormat("hh:mm:ss a");
        currentTime = Currenttime.format(new Date());
        return  currentTime;
    }

    public String getCurrentTimeWithoutAMPM()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat Currenttime = new SimpleDateFormat("hh:mm:ss");
        currentTime = Currenttime.format(new Date());
        return  currentTime;
    }





}
