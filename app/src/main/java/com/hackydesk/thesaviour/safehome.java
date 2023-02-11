package com.hackydesk.thesaviour;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import components.functiontools;

//on view created
public class safehome extends Fragment implements OnMapReadyCallback,connectionchecker.ReceiverListener {
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    private GoogleMap Mymap;
    Button notsafe;
    TextView shareJourney;
    TextView User_address;
    LinearLayout dangermode;
    Geocoder geocoder;
    Button shareaddress,Syncdata;
    String SAFE_CODE = "??IAMSAFE??";
    String JOURNEY_CODE = "@#j(0ur{}ney}}mo+2de{";
    String garbageString;
    DocumentReference dbfetcher;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    boolean trigger = false;
    FrameLayout countdowndialog;
    TextView counter;
    TextView stopCounter;
    CardView AddressContainer;
    boolean Trigger=true;
    CountDownTimer countDownForSos;
    CardView ColoStateForshareJourney,SosContainer;
    functiontools extraFeatures;
    int count = 0;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View rootView = inflater.inflate(R.layout.fragment_safehome, container, false);

        db = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        sharedPreferences = getActivity().getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        notsafe = (Button) rootView.findViewById(R.id.notsafe);
        TextView topmssg = (TextView) rootView.findViewById(R.id.topmssg);
        dangermode = (LinearLayout) rootView.findViewById(R.id.dangermode);
        shareJourney = (TextView) rootView.findViewById(R.id.sharejourney);
        User_address = (TextView) rootView.findViewById(R.id.user_address_homie);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        shareaddress = (Button) rootView.findViewById(R.id.share_address);
        Syncdata = (Button) rootView.findViewById(R.id.syncdata);
        countdowndialog = (FrameLayout) rootView.findViewById(R.id.countdowncontainer);
        counter = (TextView) rootView.findViewById(R.id.counter);
        stopCounter = (TextView) rootView.findViewById(R.id.stopBtn);
        AddressContainer = (CardView) rootView.findViewById(R.id.addresscontainer);
        ColoStateForshareJourney  = (CardView) rootView.findViewById(R.id.colorstatecard);
        SosContainer = (CardView) rootView.findViewById(R.id.sosContainer);

         extraFeatures = new functiontools(getActivity(),getContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //counter for sos activation
        countDownForSos =    new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                counter.setText("" +(millisUntilFinished / 1000));
            }
            public void onFinish() {
                countdowndialog.setVisibility(View.INVISIBLE);
                dangeralert(notsafe);
                AddressContainer.setVisibility(View.VISIBLE);
                Syncdata.setVisibility(View.VISIBLE);
            }
        };
        stopCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownForSos.cancel();
                countdowndialog.setVisibility(View.INVISIBLE);
                AddressContainer.setVisibility(View.VISIBLE);
                Syncdata.setVisibility(View.VISIBLE);
            }
        });

        Syncdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extraFeatures.checkConnection())
                {
                    extraFeatures.syncDeviceStatus();
                    Syncdata.setVisibility(View.INVISIBLE);
                }

                else {
                    Toast.makeText(getContext(), "Turn on Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //calling methods
        //applies animation if danger service is running
        runningserviceanimator();
        //LiveListener();

        //internet alert
        alerFornointernetAndGps();
        //animates buttons of running services
        alertFornoGuardiansDetected(SosContainer,Syncdata);

        final int[] counterfornotsafe = {0};
        notsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                counterfornotsafe[0]++;
                final TextView textView = topmssg;
                final String[] array = {"Hide Your Phone", "BE CAREFULL"};
                //initiate alert for danger mode
                initiateAction();
            }
        });

        shareaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAddress();
            }
        });


        Intent serviceIntent = new Intent(getContext(), sharejourneyservice.class);
        shareJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isServiceRunning(sharejourneyservice.class))
                {
                    Intent serviceIntent = new Intent(getContext(), sharejourneyservice.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        requireActivity().startForegroundService(serviceIntent);
                    }
                    sentJourneyNotification(0);
                    Toast.makeText(getContext(), "Stopped Sharing Location", Toast.LENGTH_SHORT).show();
                    requireActivity().stopService(new Intent(getContext(), sharejourneyservice.class));
                    showStopLoadingScreen();
                    System.exit(0);
                }
                else {

                    if(!isServiceRunning(DangerModeService.class))
                    {
                        if (checkConnection())
                        {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                requireActivity().startForegroundService(serviceIntent);
                                // sendNotificationtoParents();
                                sentJourneyNotification(1);
                                journeyBtnAnimation();
                            }

                            else {
                                requireActivity().startService(serviceIntent);
                                journeyBtnAnimation();
                                sentJourneyNotification(1);
                            }

                        }
                        else {
                            Toast.makeText(getContext(), "Turn  On internet To Share Journey", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "Cant Use Share Journey While Sos is Running", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rootView;
    }

    void initiateAction()
    {
        if (isServiceRunning(DangerModeService.class))
        {
            //stopping servicwe

            // Intent stopIntent = new Intent(getContext(), DangerModeService.class);
            //Toast.makeText(getContext(), "Stopping Service...", Toast.LENGTH_SHORT).show();
            String Pbnum1 =sharedPreferences.getString("bnum1","number");
            String Pbnum2 =sharedPreferences.getString("bnum2","number");
            String Pbnum3 = sharedPreferences.getString("bnum3","number");
            smssender(Pbnum1,Pbnum2,Pbnum3,currentuserid() + SAFE_CODE);
            requireActivity().stopService(new Intent(getContext(), DangerModeService.class));
            showStopLoadingScreen();
            System.exit(0);


        }
        else {

            if(!isServiceRunning(sharejourneyservice.class))
            {
                countdowndialog.setVisibility(View.VISIBLE);
                countDownForSos.start();
                AddressContainer.setVisibility(View.INVISIBLE);
                Syncdata.setVisibility(View.INVISIBLE);
            }
            else{
                Toast.makeText(getContext(), "Cant Use SOS while Sharing Journey is running ", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //returns current user id
    String currentuserid()
    {
        String authuserid="";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            authuserid = user.getUid();
        }
        return authuserid;
    }


    void dangeralert(Button button) {
        //it starts the animation
        // dangerbtnanimation(button);
        //it starts the danger mode background service;
        Intent serviceIntent = new Intent(getContext(), DangerModeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent);
           // sentSosNotification(0);
        }
        else {
            requireActivity().startService(serviceIntent);
        }
        dangerbtnanimation(notsafe);
    }

    //animatior for dangerbtn
    void dangerbtnanimation(Button button) {

        notsafe.setText("STOP");
        notsafe.post(new Runnable() {
            int i = 0;
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void run() {
                dangermode.setAlpha((float) 0.7);

                i++;
                if (i == 2) {
                    i = 0;
                    dangermode.setAlpha((float) 0.9);
                }
                notsafe.postDelayed(this, 1000);
                notsafe.setTextColor(Color.RED);
            }});

    }

    void journeyBtnAnimation()
    {
        shareJourney.setText("Stop Sharing");
        shareJourney.setTextColor(Color.BLACK);
        ColoStateForshareJourney.setBackgroundColor(Color.GREEN);
        notsafe.post(new Runnable() {
            int i = 0;
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void run() {
                shareJourney.setText("Sharing");
                ColoStateForshareJourney.setAlpha((float) 0.7);
                i++;
                if (i == 2) {
                    i = 0;
                    ColoStateForshareJourney.setAlpha((float) 1);
                    shareJourney.setText("Stop");
                }
                ColoStateForshareJourney.postDelayed(this, 1000);}});
    }


    //it continues the animation if service is running
    void runningserviceanimator()
    {
        // notsafe.setTextColor(Color.RED);
        //checks danger mode service is  running for applying animation
        if (isServiceRunning(DangerModeService.class))
        {
            Log.e("Service", "initiating animation...");
            //dangerbtnanimation(notsafe);
            notsafe.setText("STOP");
            notsafe.post(new Runnable() {
                int i = 0;
                @RequiresApi(api = Build.VERSION_CODES.S)
                @Override
                public void run() {
                    dangermode.setAlpha((float) 0.7);

                    i++;
                    if (i == 2) {
                        i = 0;
                        dangermode.setAlpha((float) 0.9);
                    }
                    notsafe.postDelayed(this, 1000);}});


        }

        if(isServiceRunning(sharejourneyservice.class))
        {
            shareJourney.setText("Stop Sharing");
            shareJourney.setTextColor(Color.BLACK);
            ColoStateForshareJourney.setBackgroundColor(Color.GREEN);
            notsafe.post(new Runnable() {
                int i = 0;
                @RequiresApi(api = Build.VERSION_CODES.S)
                @Override
                public void run() {
                    shareJourney.setText("Sharing");
                    ColoStateForshareJourney.setAlpha((float) 0.7);

                    i++;
                    if (i == 2) {
                        i = 0;
                        ColoStateForshareJourney.setAlpha((float) 1);
                        shareJourney.setText("Stop");
                    }
                    notsafe.postDelayed(this, 1000);}});
        }
    }

    //checks if service is running or not and returns true or false
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    void smssender(String primaryphnno, String bnum2, String bnum3, String Message) {

        final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Give Sms Permissions From Settings", Toast.LENGTH_SHORT).show();
        } else {
            // Set the destination phone number to the string in editText.
            String destinationAddress1 = primaryphnno;
            String destinationAddress2 = bnum2;
            String destinationAddress3 = bnum3;
            // Get the text of the SMS message.
            String smsMessage = Message;
            // Set the service center address if needed, otherwise null.
            String scAddress = null;
            // Set pending intents to broadcast
            // when message sent and when delivered, or set to null.
            PendingIntent sentIntent = null, deliveryIntent = null;
            // Use SmsManager.
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage
                    (destinationAddress1, scAddress, smsMessage,
                            sentIntent, deliveryIntent);
           // Toast.makeText(getContext(), "SENDING SAFE MESSAGE TO : " + destinationAddress1, Toast.LENGTH_SHORT).show();

            if(!Objects.equals(destinationAddress1, destinationAddress2))
            {
                smsManager.sendTextMessage
                        (destinationAddress2, scAddress, smsMessage,
                                sentIntent, deliveryIntent);
             //   Toast.makeText(getContext(), "SENDING SAFE MESSAGE TO " + destinationAddress2, Toast.LENGTH_SHORT).show();
            }
            if (!Objects.equals(destinationAddress3, destinationAddress1))
            {
                if(!Objects.equals(destinationAddress3, destinationAddress2)){
                    smsManager.sendTextMessage
                            (destinationAddress3, scAddress, smsMessage,
                                    sentIntent, deliveryIntent);
                //    Toast.makeText(getContext(), "SENDING SAFE MESSAGE TO  " + destinationAddress3, Toast.LENGTH_SHORT).show();
                }
//
            }
        }
    }

    void currentlocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
//Not the best practices to get runtime permissions, but still here I ask permissions.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
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
                    for (Location location : locationResult.getLocations()) {
                        double lat =  location.getLatitude();
                        double lng =  location.getLongitude();
                        mapUpdater(lat,lng);
                        getCompleteAddressString(lat,lng);
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";

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
                User_address.setText(strReturnedAddress.toString());
            } else {
                Log.w("My Current  address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current  address", "Canont get Address!");
        }
        return strAdd;
    }

    //its loads map and reloads map
    void mapUpdater(double lat , double lng) {
//        Mymap.getUiSettings().setZoomGesturesEnabled(true);
//        Mymap.getUiSettings().setRotateGesturesEnabled(true);
        LatLng position = new LatLng(lat, lng);
        //Mymap.addMarker(new MarkerOptions().position(position).title("Location Fetched"));
        Mymap.clear();
        Marker m1 =  Mymap.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title("User Position")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_region_64__2_)));

        assert m1 != null;
        m1.setPosition(position);

        // Mymap.moveCamera(CameraUpdateFactory.newLatLng(position));
        Mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,16f));
    }

    void showStopLoadingScreen()
    {
        Intent intent = new Intent(getContext(), StopActivity.class);
        startActivity(intent);
    }

    void shareAddress()
    {

        String Address = User_address.getText().toString();

        if(Address.length()<15)
        {
            Toast.makeText(getContext(), "Wait Address is Loading", Toast.LENGTH_SHORT).show();
        }

        else{
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,"My Address is \n" + Address + "Shared With The Saviour");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }

    }



    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 32234223);

            }
        }
    }


    void sentJourneyNotification(int code)
    {
       SharedPreferences sharedPreferences = getContext().getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
       String childname = sharedPreferences.getString("profile_name","User");
//code 0 for off
//code 1 for on
        functiontools journeynotification = new functiontools(getActivity(),getContext());
      if(code==0)
      {
          journeynotification.sendOnlineMesageToparent("The Saviour",childname + " Stopped Sharing Location");
      }
      if (code==1)
      {
          journeynotification.sendOnlineMesageToparent("The Saviour",childname + " is Sharing Live Location And Device Status with you Tap To see info");
      }

    }





    public boolean checkConnection() {
        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();
        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
        // Initialize listener
        connectionchecker.Listener = this;
        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        //sending what to do when net  on
        return isConnected;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Mymap = googleMap;
        currentlocation();
    }


    void alerFornointernetAndGps()
    {
        functiontools coreextra  = new functiontools(getActivity(),getContext());

        if (!checkConnection())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("No Internet");
            builder.setMessage("Turn Internet On For More Accurate Data , if Not Possible Don't Worry i  Can Do Many things offline too");
            builder.setPositiveButton("ok",(dialog, which) ->
            {
                builder.setCancelable(true);
            });

            builder.show();

        }
       else if (!coreextra.checkGpsStatus())
        {
            coreextra.requestGPSPermission();
        }
    }


    void alertFornoGuardiansDetected(CardView c1 , Button b1 )
    {
        if (extraFeatures.checkConnection())
        {
            extraFeatures.totalGuardiansCounter(c1,b1);
        }

    }

    public void onNetworkChange(boolean isConnected) {
        // Toast.makeText(getContext(), "huss.. Interet On", Toast.LENGTH_SHORT).show();
    }
}