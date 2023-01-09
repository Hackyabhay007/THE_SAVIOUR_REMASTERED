package com.example.thesaviour;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AllPermissionAtOnce extends AppCompatActivity {
//firebase instance

    private Button microbtn;
    private Button locbtn;
    private Button smsbtn;
    private Button callbtn;
    private Button camerabtn;
    private Button Proceedbtn;


    Activity activity;

    //request codes for Permissions
    final private  int REQ_CODE_FOR_MICROPHONE=2789;
    final private  int REQ_CODE_FOR_LOCATION_COARSE=2790;
    final private  int REQ_CODE_FOR_LOCATION_FINE=2799;
    final private  int REQ_CODE_FOR_SMS_SEND=2791;
    final private  int REQ_CODE_FOR_SMS_RECEIVE=2710;
    final private  int REQ_CODE_FOR_CALL=2792;
    final private  int REQ_CODE_FOR_CAMERA=2793;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_permission_at_once);

        microbtn = findViewById(R.id.microphonebtn);
        locbtn = findViewById(R.id.locbtn);
        smsbtn = findViewById(R.id.smsbtn);
        callbtn = findViewById(R.id.callbtn);
        camerabtn = findViewById(R.id.camerabtn);
        Proceedbtn = findViewById(R.id.ProceedBtn);

        activity = AllPermissionAtOnce.this;
        //must functions
        //it redirects to home if login and All permissions Allowed
        //otherwise give warnings to Give Permission
            RedirectToHome();


//this is Important for Parent mode to Receive SMS sos
        String [] permission ={Manifest.permission.RECEIVE_SMS};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission,239);
        }


        microbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(),Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.RECORD_AUDIO},REQ_CODE_FOR_MICROPHONE);
                }
                else {

                    microbtn.setText("GRANTED");

                }
            }
        });
        locbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE_FOR_LOCATION_FINE);
                    ActivityCompat.requestPermissions( activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_CODE_FOR_LOCATION_COARSE);
                }
                else {

                    locbtn.setText("GRANTED");

                }
            }
        });

        smsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(),Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getBaseContext(),Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.SEND_SMS}, REQ_CODE_FOR_SMS_SEND);
                    ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.RECEIVE_SMS}, REQ_CODE_FOR_SMS_RECEIVE);

                }
                else {
                    smsbtn.setText("GRANTED");

                }
            }
        });

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},REQ_CODE_FOR_CALL);
                    }
                    else
                    {
                        callbtn.setText("GRANTED");

                    }
                }

            }
        });

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},REQ_CODE_FOR_CAMERA);
                }
                else
                {
                    camerabtn.setText("GRANTED");
                }
            }
        });

        Proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(AllPermissionChecker() && user == null ){
                    Intent intent = new Intent(activity, firsttime.class);
                    startActivity(intent);
                }

                else {
                    Intent intent = new Intent(activity, homie.class);
                    startActivity(intent);
                }

            }
        });
    }


    //it will proceed Automatically user to home if login and allowed all permissions

    void RedirectToHome()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(AllPermissionChecker() && user == null ){
            Intent intent = new Intent(activity, firsttime.class);
            startActivity(intent);
        }
        else if (AllPermissionChecker() && user != null ){
            Intent intent = new Intent(activity, homie.class);
            startActivity(intent);
        }
    }

//checks if all the permissions are ON or OFF
    boolean AllPermissionChecker()

    {
        boolean PermissionOutput=false;

        if ( ActivityCompat.checkSelfPermission(getBaseContext(),Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity, "Allow Location Permission", Toast.LENGTH_SHORT).show();
        }


        if (ActivityCompat.checkSelfPermission(getBaseContext(),Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ){
            Toast.makeText(activity, "Allow SMS Permission", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity, "Allow CALL Permission", Toast.LENGTH_SHORT).show();

        }

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity, "Allow CAMERA Permission", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity, "Allow MICROPHONE Permission", Toast.LENGTH_SHORT).show();
        }

        if ( ActivityCompat.checkSelfPermission(getBaseContext(),Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getBaseContext(),Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        {
            PermissionOutput = true;
        }

        //else is already assigned
        //Toast.makeText(activity, String.valueOf(PermissionOutput) , Toast.LENGTH_SHORT).show();
        return PermissionOutput;
    }


}