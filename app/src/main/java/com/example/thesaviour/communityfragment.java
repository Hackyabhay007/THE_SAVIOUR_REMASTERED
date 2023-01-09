package com.example.thesaviour;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class communityfragment extends Fragment {
    WebView webView;
    String  authuserid;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_communityfragment, container,false);
        webView = (WebView) rootView.findViewById(R.id.CommunityMapG);

        maploader();
        return rootView;

    }

    //its loads map and reloads map
    void maploader(){
        int ReloadMapAfter=30000;
        webView.loadUrl("https://hackyabhay007.github.io/TheSaviourMapBox/??"+currentuserid());
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

    String currentuserid()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
//            // Check if user's email is verified
//            boolean emailVerified = user.isEmailVerified();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            authuserid = user.getUid();

        }
        return authuserid;
    }
}