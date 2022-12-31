package com.example.thesaviour;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class bodygaurds extends Fragment {
FirebaseFirestore db;
//dont remove it it will affect primarygaurds and firestore database
FirebaseFirestore db2 = FirebaseFirestore.getInstance();
    DocumentReference dbfetcher;
    Boolean bprime1;
Boolean bprime2;
Boolean bprime3;
Switch primary1;
Switch primary2;
Switch primary3;
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    TextView Bname1;
    TextView Bname2;
    TextView Bname3;
    TextView Bnum1;
    TextView Bnum2 ;
    TextView Bnum3;
    TextView Primarystatus;

FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bodygaurds, container,false);


            db = FirebaseFirestore.getInstance();
         dbfetcher = db.collection("Users").document(FirebaseAuth.getInstance().getUid());
         sharedPreferences = getActivity().getSharedPreferences("Thesaviour",Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();

         Bname1 = (TextView) rootView.findViewById(R.id.bname1);
         Bname2 = (TextView) rootView.findViewById(R.id.bname2);
         Bname3 = (TextView) rootView.findViewById(R.id.bname3);
         Bnum1 = (TextView) rootView.findViewById(R.id.bnum1);
         Bnum2 = (TextView) rootView.findViewById(R.id.bnum2);
         Bnum3 = (TextView) rootView.findViewById(R.id.bnum3);
         Primarystatus = (TextView) rootView.findViewById(R.id.primarystatus);
         primary1 = (Switch) rootView.findViewById((R.id.primaryb1));
         primary2 = (Switch) rootView.findViewById((R.id.primaryb2));
         primary3 = (Switch) rootView.findViewById((R.id.primaryb3));

        //if user does not select any bodybaurd as primary 1st will be selected automatically and send to the server
       autoprmrybodygaurd();
       //it loads the last saved state of switches
        LoadLastState();




        primary1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (primary1.isChecked())
                {

                    primary2.setChecked(false);
                    primary3.setChecked(false);
                    PrimaryBodyGaurdSelector("true","false","false");
                    savestateforswitch(true,false,false);
                }
            }
        });
        primary2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (primary2.isChecked())
                {

                    primary1.setChecked(false);
                    primary3.setChecked(false);
                    PrimaryBodyGaurdSelector("false","true","false");
                    savestateforswitch(false,true,false);
                }

            }
        });

        primary3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (primary3.isChecked())
                {

                    primary1.setChecked(false);
                    primary2.setChecked(false);
                    PrimaryBodyGaurdSelector("false","false","true");
                    //save instance in sharedpref
                    savestateforswitch(false,false,true);
                }
            }
        });

        //using shared preference setting data from shared data

        String primarytext=sharedPreferences.getString("primarytext","name");
        String toUpperCaseprimary = primarytext.toUpperCase();
        String pbname2 = sharedPreferences.getString("bname2","name");
        String pbname3= sharedPreferences.getString("bname3","name");
        String Pbnum1 =sharedPreferences.getString("bnum1","number");
        String Pbnum2 =sharedPreferences.getString("bnum2","number");
        String Pbnum3 = sharedPreferences.getString("bnum3","number");
        Primarystatus.setText(toUpperCaseprimary);
        Bname1.setText(primarytext);
        Bname2.setText(pbname2);
        Bname3.setText(pbname3);
        Bnum1.setText(Pbnum1);
        Bnum2.setText(Pbnum2);
        Bnum3.setText(Pbnum3);

        // Source can be CACHE, SERVER, or DEFAULT
       // Source source = Source.CACHE;
        dbfetcher.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            String bodygaurds = (String) documentSnapshot.get("bodyguards");
                            bodygaurds.toUpperCase(Locale.ROOT);
                            dataseperator(bodygaurds);
                            //Toast.makeText(getContext(), "Bodygaurds Fetched ", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "User not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
                return rootView;
                }

    void dataseperator(String bodygaurds){
                String[] bdata = bodygaurds.split("[,]", 0);
                int datalength = bdata.length;
                if (datalength==9)
                {
                    String bname1=bdata[0];
                    String bnum1=bdata[1];
                    Boolean b1= Boolean.valueOf(bdata[2]);
                    String bname2=bdata[3];
                    String bnum2=bdata[4];
                    Boolean b2= Boolean.valueOf(bdata[5]);
                    String bname3=bdata[6];
                    String bnum3=bdata[7];
                    Boolean b3= Boolean.valueOf(bdata[8]);

                    //adding data to database for other use

                    if (b1)
                    {
                        editor.putString("primarytext",bname1);
                        editor.putString("primaryphnno",bnum1);
                    }
                    if (b2)
                    {
                        editor.putString("primarytext",bname2);
                        editor.putString("primaryphnno",bnum2);
                    }
                    if (b3)
                    {
                        editor.putString("primarytext",bname3);
                        editor.putString("primaryphnno",bnum2);
                    }
                    editor.putString("bname1",bname1);
                    editor.putString("bname2",bname2);
                    editor.putString("bname3",bname3);
                    editor.putString("bnum1",bnum1);
                    editor.putString("bnum2",bnum2);
                    editor.putString("bnum3",bnum3);
                    editor.putBoolean("b1",b1);
                    editor.putBoolean("b2",b2);
                    editor.putBoolean("b3",b3);
                    editor.apply();
                }
                else {
                    Toast.makeText(getContext(), "DATA NOT FOUND", Toast.LENGTH_SHORT).show();
                }
            }

            //this function edit the only primary bodygaurddata
            void PrimaryBodyGaurdSelector(String pb1 ,String pb2 ,String pb3){

            DocumentReference primaryfetcher = db.collection("Users").document(FirebaseAuth.getInstance().getUid());
                primaryfetcher.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                 String bodygaurds = (String) document.get("bodyguards");
                                 String[] bdata= bodygaurds.split("[,]", 0);
                                 StringBuilder updated= new StringBuilder();
                                 bdata[2]=pb1;
                                 bdata[5]=pb2;
                                 bdata[8]=pb3;

                                for (int i=0;i<bdata.length;i++) {
                                    if (bdata.length-1>i)
                                    {
                                        updated.append(bdata[i]).append(",");
                                    }
                                    else {
                                        updated.append(bdata[i]);
                                    }
                                }
                                 Map<String, Object> NewPrimarybodygaurd = new HashMap<>();
//
                                NewPrimarybodygaurd.put("bodyguards", updated.toString());

                                db.collection("Users").document(firebaseAuth.getInstance().getUid()).update(NewPrimarybodygaurd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            //    Toast.makeText(getContext(), "Primary BodyGuard Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "No BodyGuards Found", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(getContext(), "Get  Failed With "+ task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            void autoprmrybodygaurd()
            {
                if ( !primary1.isChecked() && !primary1.isChecked() && !primary1.isChecked())
                {
                    primary1.setChecked(true);
                    PrimaryBodyGaurdSelector("true","false","false");

                }

            }

            void savestateforswitch(boolean s1,boolean s2,boolean s3)
            {
                editor = sharedPreferences.edit();
                editor.putBoolean("bswitch1",s1);
                editor.putBoolean("bswitch2",s2);
                editor.putBoolean("bswitch3",s3);
                editor.apply();
            }

            void LoadLastState()
            {
                primary1.setChecked(sharedPreferences.getBoolean("bswitch1",true));
                primary2.setChecked(sharedPreferences.getBoolean("bswitch2",false));
                primary3.setChecked(sharedPreferences.getBoolean("bswitch3",false));
            }
//




    }