package com.example.thesaviour;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;



public class settings extends Fragment {

    public settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container,false);
        Button addprotector = (Button) rootView.findViewById(R.id.addbodygaurd);
        Button logout = (Button) rootView.findViewById(R.id.logout_btn);
        Button parent_mode = (Button) rootView.findViewById(R.id.parent_mode);
        Button profile = (Button) rootView.findViewById(R.id.profile_user);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(),firsttime.class));
                SharedPreferences preferences = getActivity().getSharedPreferences("Thesaviour", 0);
                SharedPreferences.Editor editor = preferences.edit();
                Toast.makeText(getContext(), "USER DATA CLEARED", Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.apply();
                FirebaseAuth.getInstance().signOut();

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserProfile.class);
                startActivity(intent);
            }
        });

        parent_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ParentMode.class);
                startActivity(intent);
            }
        });

        addprotector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),bodygaurdactivity.class));
            }
        });
        return rootView;
    }

    void copytoclipboard()
    {

    }
}