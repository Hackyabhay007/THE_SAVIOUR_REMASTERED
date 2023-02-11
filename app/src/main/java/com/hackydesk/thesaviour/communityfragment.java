package com.hackydesk.thesaviour;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import components.Loader;


public class communityfragment extends Fragment {

    String  authuserid;
    TextView useraddress;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Geocoder geocoder ;
    RatingBar Girlssafety,Policesupport,Streetlights,Crimerate;
    Button ratingsubmitbutton;
    double latitude;
    double longitude;
    String geohashdata;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_communityfragment, container,false);
        useraddress = (TextView)  rootView.findViewById(R.id.user_address_homie);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
         Girlssafety = (RatingBar) rootView.findViewById(R.id.ratingBar);
        Policesupport = (RatingBar) rootView.findViewById(R.id.ratingBar2);
        Streetlights = (RatingBar) rootView.findViewById(R.id.ratingBar3);
        Crimerate = (RatingBar) rootView.findViewById(R.id.ratingBar4);
        ratingsubmitbutton =(Button) rootView.findViewById(R.id.button);





        currentlocation();

        ratingsubmitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    formsubmit();
            }
        });



        return rootView;
    }

    String currentuserid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            authuserid = user.getUid();
        }
        return authuserid;
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
        locationRequest.setInterval(50000);
        locationRequest.setFastestInterval(50000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//instantiating the LocationCallBack
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    if (locationResult == null) {
                        return;
                    }
                    //Showing the latitude, longitude and accuracy on the home screen.

                    for (Location location : locationResult.getLocations()) {

                        double lat =  location.getLatitude();
                        double lng =  location.getLongitude();
                         geohashdata = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));
                            getCompleteAddressString(lat,lng);
                            latitude =lat;
                            longitude =lng;

                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    //decode address from lat long and set at Address
//    void AddressDecoder(double MyLat , double MyLong ) throws IOException {
//
//        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
//        List<Address> addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
//        String cityName = addresses.get(0).getAddressLine(0);
//        String stateName = addresses.get(0).getAddressLine(1);
//        String countryName = addresses.get(0).getAddressLine(2);
//        if (addresses.isEmpty()) {
//            useraddress.setText("Waiting for Location");
//        } else {
//            if (addresses.size() > 0) {
//                useraddress.setText(cityName);
//                //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
//            }
//        }
//
//    }

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
                useraddress.setText(strReturnedAddress.toString());
            } else {
                Log.w("My Current  address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current  address", "Canont get Address!");
        }
        return strAdd;
    }

    void formsubmit()  {
        Loader loader = new Loader(getActivity());
        loader.startLoader();

     if (getCompleteAddressString(latitude,longitude).length()>10)
     {
         float girls,policesupport,streetlights,crimerate,Score;
         girls  = Girlssafety.getRating();
         policesupport = Policesupport.getRating();
         streetlights = Streetlights.getRating();
         crimerate = Crimerate.getRating();
         Score = girls+policesupport+streetlights+crimerate;

         if (Score==0)

         {
             loader.dismissLoader();
             Toast.makeText(getContext(), "Provide All Ratings First", Toast.LENGTH_SHORT).show();
         }

         else {
             FirebaseFirestore db = FirebaseFirestore.getInstance();
             Map<String, Object> data = new HashMap<>();
             data.put("girlssafetyscore",girls);
             data.put("policesupport",policesupport);
             data.put("streelightsscore",streetlights);
             data.put("crimerate",crimerate);
             data.put("geohash", geohashdata);
             data.put("latitide", latitude);
             data.put("longitude",longitude);
             data.put("totalscore",Score);
             data.put("address",getCompleteAddressString(latitude,longitude));

             db.collection("citysafetydata").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                         @Override
                         public void onSuccess(DocumentReference documentReference) {
                             loader.dismissLoader();
                             Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                             Toast.makeText(getContext(), "Data Submitted Successfully", Toast.LENGTH_SHORT).show();
                             AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                             builder.setTitle("Thank-you for submitting data");
                             builder.setMessage("You can Update data or Review another Area too");
                             builder.setIcon(R.drawable.mainlog_transparent_bg);
                             builder.setPositiveButton("Welcome", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                 }
                             });
                             builder.show();
                             // Toast.makeText(getContext(), "Thanks , If You Want To Update You Can", Toast.LENGTH_SHORT).show();

                         }
                     })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             loader.dismissLoader();
                             Toast.makeText(getContext(), "Failed Check Your Internet", Toast.LENGTH_SHORT).show();
                         }
                     });

         }

     }

     else {
         Toast.makeText(getContext(), "Wait Loading adrress...", Toast.LENGTH_SHORT).show();
     }

    }


}