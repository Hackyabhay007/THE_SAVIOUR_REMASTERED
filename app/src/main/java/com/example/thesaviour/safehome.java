package com.example.thesaviour;

import static android.content.Context.ACTIVITY_SERVICE;

import android.Manifest;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.util.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

//on view created
public class safehome extends Fragment {
    CardView dangermode;
    Button notsafe;
    TextView output;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_safehome, container, false);
         notsafe = (Button) rootView.findViewById(R.id.notsafe);
        TextView topmssg = (TextView) rootView.findViewById(R.id.topmssg);
         dangermode = (CardView) rootView.findViewById(R.id.dangermode);
         output = (TextView) rootView.findViewById(R.id.output);


        //calling methods
        //applies animation if danger service is running

        animatorfordangermode();
        final int[] counterfornotsafe = {0};
        notsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                counterfornotsafe[0]++;
                if (counterfornotsafe[0] == 5) {
                    final TextView textView = topmssg;
                    final String[] array = {"Hide Your Phone", "BE CAREFULL"};
                    textView.post(new Runnable() {
                        int i = 0;
                        @RequiresApi(api = Build.VERSION_CODES.S)
                        @Override
                        public void run() {
                            dangermode.setAlpha((float) 0.7);
                            textView.setText(array[i]);
                            i++;
                            if (i == 2) {
                                i = 0;
                                dangermode.setAlpha((float) 0.9);
                            }
                            textView.postDelayed(this, 1000);}});
                    //initiate alert for danger mode
                    dangeralert(notsafe);
                    iamsafe();
                }
            }
        });
        return rootView;
    }

    void dangeralert(Button button) {
        //it starts the animation
        dangerbtnanimation(button);
        //it starts the danger mode background service;
        Intent serviceIntent = new Intent(getContext(), DangerModeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent);
        }
    }

    //animatior for dangerbtn
    void dangerbtnanimation(Button button) {

        ColorDrawable[] color = {new ColorDrawable(Color.BLUE), new ColorDrawable(Color.WHITE)};
        TransitionDrawable trans = new TransitionDrawable(color);
        //This will work also on old devices. The latest API says you have to use setBackground instead.
        trans.startTransition(5000);
        RotateAnimation rotate = new RotateAnimation(180, 720, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);
        button.startAnimation(rotate);
        button.setText("ACTIVATED");
        button.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        dangermode.setCardBackgroundColor(Color.RED);
        button.setTextColor(Color.RED);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(600);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        dangermode.setAnimation(animation);
        //button.setCardBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_light));

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

    void animatorfordangermode()
    {
        //checks danger mode service is  running for applying animation
        if (isServiceRunning(DangerModeService.class))
        {
            Log.e("Service", "initiating animation...");
            dangerbtnanimation(notsafe);
            notsafe.setText("ACTIVATED");
            iamsafe();
        }
    }



    //if service is running this method will convert bottom text to A  stop Danger Button
    private  void iamsafe()
    {
        if (isServiceRunning(DangerModeService.class))
        {
            Log.e("Service", "STOPPED DANGER MODE");
            output.setText("Tap 5 TIMES TO STOP");
            final int[] clickcounter = {0};
            output.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickcounter[0]++;
                    if (clickcounter[0]==5)
                    {
                        getContext().stopService(new Intent(getContext(), DangerModeService.class));
                        clickcounter[0]=0;
                        output.setText("DANGER MODE STOPPED");
                        getActivity().finish();
                        System.exit(0);
                    }
                }
            });


        }

    }




}