package com.hackydesk.thesaviour;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import components.Loader;
import components.functiontools;

public class userregister extends AppCompatActivity implements connectionchecker.ReceiverListener {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Loader pd;
    Button rtrnbacktomain;
    Button register, SelectContact;
    int REQUEST_READ_CONTACTS_PERMISSION = 2036;
    int REQUEST_READ_CONTACT = 2034;
    Uri uri = Uri.parse("content://contacts");
    Intent intent = new Intent(Intent.ACTION_PICK, uri);
    boolean permissionboolean = false;
    String currentDate;
    String currentTime;
    String Fname;
    String Email;
    String Phonenumber;
    String Password;
    String GuardianFname;
    String GPhonenumber;
    String BodyguardsData;

    EditText fullname, password, phonenumber, email, gfullname, gphonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregister);
        rtrnbacktomain = findViewById(R.id.anon);
        register = findViewById(R.id.fregister);
        fullname = findViewById(R.id.fullname);
        phonenumber = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.usernamelogin);
        gfullname = findViewById(R.id.gfullname);
        gphonenumber = findViewById(R.id.gphonenumber);
        SelectContact = findViewById(R.id.selectcontact);
        pd = new Loader(userregister.this);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat Currentdate = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat Currenttime = new SimpleDateFormat("hh:mm:ss a");
        currentDate = Currentdate.format(new Date());
        currentTime = Currenttime.format(new Date());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((userregister.this));

        //  userLocationAndData();


        rtrnbacktomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userregister.this, login.class);
                startActivity(intent);
            }
        });


        //subscribe to notifications for saviours topic

        FirebaseMessaging.getInstance().subscribeToTopic("saviours")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("TAG", msg);
                        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

        SelectContact.setOnClickListener(v -> {

            Toast.makeText(this, "Choose Number Which is being Used in Guardian's Phone", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(userregister.this,
                        new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
            } else {
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_READ_CONTACT);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd.startLoader();
                //reading data from textfeilds
                Fname = fullname.getText().toString();
                Email = email.getText().toString();
                Phonenumber = phonenumber.getText().toString();
                Password = password.getText().toString();
                GuardianFname = gfullname.getText().toString();
                GPhonenumber = gphonenumber.getText().toString();
                BodyguardsData = GuardianFname + "," + GPhonenumber + "," + "true" + "," + GuardianFname + "," + GPhonenumber + "," + "false" + "," + GuardianFname + "," + GPhonenumber + "," + "false";

                if (Fname.isEmpty() || Email.isEmpty() || Phonenumber.isEmpty() || Password.isEmpty()) {
                    pd.dismissLoader();
                    Toast.makeText(userregister.this, "Please fill all the feilds", Toast.LENGTH_SHORT).show();
                } else if (Phonenumber.length() < 10) {
                    pd.dismissLoader();
                    Toast.makeText(userregister.this, "Input Atleast 10 Digit in Phone Number", Toast.LENGTH_SHORT).show();
                } else if (Password.length() < 6) {
                    pd.dismissLoader();
                    Toast.makeText(userregister.this, "Password Should Have Atleaset 6 Digits", Toast.LENGTH_SHORT).show();
                } else if (GuardianFname.isEmpty() || GPhonenumber.isEmpty()) {
                    {
                        pd.dismissLoader();
                        Toast.makeText(userregister.this, "Select Guardian its Required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (checkGpsAndInternetpermissions()) {
                        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                     //   Toast.makeText(userregister.this, "sabse pahle", Toast.LENGTH_SHORT).show();
                                        userLocationAndData();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismissLoader();
                                        Toast.makeText(userregister.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

    }

    boolean checkGpsAndInternetpermissions() {

        functiontools functiontools = new functiontools(getApplicationContext());
        functiontools functiontoolsextra = new functiontools(userregister.this, getApplicationContext());

        if (functiontools.checkGpsStatus()) {
            if (functiontoolsextra.checkConnection()) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(userregister.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Toast.makeText(getApplicationContext(), "Allow Location Permissions From Settings", Toast.LENGTH_SHORT).show();
                    pd.dismissLoader();
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
                permissionboolean = false;
                pd.dismissLoader();
            }
        } else {
            functiontoolsextra.requestGPSPermission();
            pd.dismissLoader();
        }
        return permissionboolean;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_READ_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);

                gfullname.setText(name);
                gphonenumber.setText((PhoneNumberCleaner(number)));
                Log.d("Contact got", "ZZZ number : " + number + " , name : " + name);

            }
        }
    }

    //current location check and location track service activator
    boolean trigger=true;
    void userLocationAndData() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        //Not the best practices to get runtime permissions, but still here I ask permissions.

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            pd.dismissLoader();
        } else {
            if (checkGpsAndInternetpermissions()) {

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//Not the best practices to get runtime permissions, but still here I ask permissions.
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(userregister.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions((userregister.this), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
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

                            if(trigger)
                            {
                                int BatteryPercentage = 0;
                                // Toast.makeText(getApplicationContext(, Double.toString(lat) + Double.toString(lng), Toast.LENGTH_SHORT).show();
                                String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
                                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                                Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
                                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                                // Add the hash and the lat/lng to the document. We will use the hash
                                // for queries and the lat/lng for distance comparisons.
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("geohash", hash);
                                updates.put("lat", location.getLatitude());
                                updates.put("lng", location.getLongitude());
                                updates.put("date", currentDate);
                                updates.put("time", currentTime);
                                updates.put("accuracy", location.getAccuracy());
                                updates.put("Device Speed", location.getSpeed());
                                updates.put("BatteryStatus", level);
                                //  Toast.makeText(getApplicationContext(), "came here2", Toast.LENGTH_SHORT).show();
                                db.collection("Users").document((Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))).set(new usermodelstore(Fname, Phonenumber, Email, BodyguardsData));
                                db.collection("Users").document(FirebaseAuth.getInstance().getUid()).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismissLoader();
                                        Toast.makeText(userregister.this, "REGISTERED SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(userregister.this, AllPermissionAtOnce.class));
                                        //  Toast.makeText(userregister.this, "NOW YOU CAN LOGIN", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                trigger =false;

                            }
                            }

                    }

                };
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());


            }

        }
    }

    String PhoneNumberCleaner(String number)
    {
        String newnumber = number.replaceAll("[^0-9]", "");
        if(newnumber.length()==13)
        {
            newnumber =   newnumber.replaceFirst("910","");
        }
        else if(newnumber.length()==12)
        {
            newnumber =   newnumber.replaceFirst("91","");
        }

        else if(newnumber.length()==11)
        {
            newnumber =   newnumber.replaceFirst("0","");
        }
            return newnumber;
    }




    @Override
    public void onNetworkChange(boolean isConnected) {

    }
}