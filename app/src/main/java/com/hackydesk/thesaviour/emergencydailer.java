package com.hackydesk.thesaviour;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class emergencydailer extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_emergencydailer, container,false);

        TextView n1 = (TextView) rootView.findViewById(R.id.n1);
        TextView n2 = (TextView) rootView.findViewById(R.id.n2);
        TextView n3 = (TextView) rootView.findViewById(R.id.n3);
        TextView n4 = (TextView) rootView.findViewById(R.id.n4);
        TextView n5 = (TextView) rootView.findViewById(R.id.n5);
        TextView n6 = (TextView) rootView.findViewById(R.id.n6);
        TextView n7 = (TextView) rootView.findViewById(R.id.n7);
        TextView n8 = (TextView) rootView.findViewById(R.id.n8);
        TextView n9 = (TextView) rootView.findViewById(R.id.n9);
        TextView n10 = (TextView) rootView.findViewById(R.id.n10);
        LinearLayout l1 = (LinearLayout) rootView.findViewById(R.id.l1);
        LinearLayout l2 = (LinearLayout) rootView.findViewById(R.id.l2);
        LinearLayout l3 = (LinearLayout) rootView.findViewById(R.id.l3);
        LinearLayout l4 = (LinearLayout) rootView.findViewById(R.id.l4);
        LinearLayout l5 = (LinearLayout) rootView.findViewById(R.id.l5);
        LinearLayout l6 = (LinearLayout) rootView.findViewById(R.id.l6);
        LinearLayout l7 = (LinearLayout) rootView.findViewById(R.id.l7);
        LinearLayout l8 = (LinearLayout) rootView.findViewById(R.id.l8);
        LinearLayout l9 = (LinearLayout) rootView.findViewById(R.id.l9);
        LinearLayout l0 = (LinearLayout) rootView.findViewById(R.id.l10);


        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n1);
            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n2);
            }
        });
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n3);
            }
        });
        l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n4);
            }
        });
        l5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n5);
            }
        });
        l6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n6);
            }
        });
        l7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n7);
            }
        });
        l8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n8);
            }
        });
        l9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n9);
            }
        });
        l0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatecall(n10);
            }
        });
        localdailernumbers o1 =new localdailernumbers(1,"NATIONAL EMERGENCY NUMBER","112");
        localdailernumbers o2 =new localdailernumbers(2,"POLICE","100");
        localdailernumbers o3 =new localdailernumbers(3,"FIRE","101");
        localdailernumbers o4 =new localdailernumbers(4,"AMBULANCE","102");
        localdailernumbers o5 =new localdailernumbers(5,"Disaster Management Services","108");
        localdailernumbers o6 =new localdailernumbers(6,"Women Helpline","1091");
        localdailernumbers o7 =new localdailernumbers(7,"CYBER CRIME HELPLINE","155620");
        localdailernumbers o8 =new localdailernumbers(8,"Children In Difficult Situation","1098");
        localdailernumbers o9 =new localdailernumbers(9,"Road Accident Emergency Service","1073");
        localdailernumbers o10 =new localdailernumbers(10,"Missing Child And Women","1094");
        localdailernumbers [] contacts={o1,o2,o3,o4,o5,o6,o7,o8,o9,o10};

        return rootView;
    }
   void initiatecall(TextView phnnumber)
    {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},278381);
        }
        else
        {
            String primaryphnno = phnnumber.getText().toString();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + primaryphnno));
            startActivity(intent);
        }


    }
}