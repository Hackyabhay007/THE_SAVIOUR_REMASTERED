package com.hackydesk.thesaviour;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import components.functiontools;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class AllPermissionAtOnce extends AppCompatActivity {
    //firebase instance
    ArrayList<String> permissionsList;
    String[] permissionsStr = {Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.POST_NOTIFICATIONS};
    int permissionsCount = 0;
    ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            permissionsCount = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                    permissionsList.add(permissionsStr[i]);
                                } else if (!hasPermission(getApplicationContext(), permissionsStr[i])) {
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
                               RedirectToHome();
                                // Toast.makeText(getApplicationContext(), "All Permissions Granted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    private Button microbtn;
    private Button locbtn;
    private Button smsbtn;
    private Button callbtn;
    private Button camerabtn;
    private Button Proceedbtn;
    private TextView permissioninfo;
    Intent intent;
    AlertDialog alertDialog;
    functiontools extra;
    Activity activity;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_permission_at_once);
        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        //ask for permissions
        askForPermissions(permissionsList);
        extra = new functiontools(AllPermissionAtOnce.this,getApplicationContext());
        microbtn = findViewById(R.id.microphonebtn);
        locbtn = findViewById(R.id.locbtn);
        smsbtn = findViewById(R.id.smsbtn);
        callbtn = findViewById(R.id.callbtn);
        camerabtn = findViewById(R.id.camerabtn);
        Proceedbtn = findViewById(R.id.ProceedBtn);
        activity = AllPermissionAtOnce.this;
        permissioninfo = findViewById(R.id.permissioninfo);
        //must functions
        //it redirects to home if login and All permissions Allowed
        //otherwise give warnings to Give Permission

       SharedPreferences sharedPreferences = AllPermissionAtOnce.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        String child1userid = sharedPreferences.getString("Parentchild1UserId", "");
//checks parent is logged in or not
        if (child1userid.length()>9)
        {
            //Toast.makeText(this, child1userid, Toast.LENGTH_SHORT).show();
            permissioninfo.setText("Please Allow All The Permsiions To Run the App Smoothly .Location Permission Is Used to get Current Location and calculate distance between user and guardian ,SMS permission is used to sent secret code to user ,Call permisiion Is used to Call Emergency Numbers");
             intent = new Intent(AllPermissionAtOnce.this, parenthome.class);
        }
        else {
            intent = new Intent(AllPermissionAtOnce.this, homie.class);
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                RedirectToHome();
            }
        }, 10);
//this is Important for Parent mode to Receive SMS sos
        Proceedbtn.setOnClickListener(view -> {


            if (AllPermissionChecker())
            {
                startActivity(intent);
            }
            else {





                AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.drawable.mainlog_transparent_bg);
                builder.setMessage("This Application Requires These Permissions Other Wise ,App May Crash")
                        .setCancelable(false).setTitle("Mandatory Permissions")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(activity, "Allow All The Permissions Excluding Contact", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
//
        });
    }
    //it will proceed Automatically user to home if login and allowed all permissions

    void RedirectToHome() {
        if (AllPermissionChecker()) {
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog_Alert_Bridge);
            builder.setTitle("Permission required")
                    .setMessage("Some permissions are needed to be allowed to use this app without any problems.").setCancelable(false)
                    .setPositiveButton("Allow Permissions", (ans, yes) -> {
                        permissionsList = new ArrayList<>();
                        permissionsList.addAll(Arrays.asList(permissionsStr));
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    });
        }
    }
    //checks if all the permissions are ON or OFF
    boolean AllPermissionChecker() {

        if(!extra.checkGpsStatus())
        {
            extra.requestGPSPermission();
        }
        return ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED && extra.checkGpsStatus();

    }

    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissionDialog() {
        //addresstext.setText("Showing settings dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog_Alert_Bridge);
        builder.setTitle("Permission required")
                .setMessage("Some permissions are needed to be allowed to use this app without any problems.").setCancelable(false)
                .setPositiveButton("Allow", (ans, yes) -> {
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


}