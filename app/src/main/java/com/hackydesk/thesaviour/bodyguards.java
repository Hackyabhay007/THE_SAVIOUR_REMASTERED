package com.hackydesk.thesaviour;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import components.Loader;
import components.functiontools;

public class bodyguards extends Fragment implements connectionchecker.ReceiverListener {
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
    TextView Bnum2;
    TextView Bnum3;
    TextView Primarystatus;
    TextView BodyGuardname2,BodyGuardnumber2,BodyGuardname3,BodyGuardnumber3;
    CardView b1card,b2card,b3card;
    Button addguardian2,addguardian3;
    Dialog dialog2;
    Dialog dialog3;
    Loader loader;
    functiontools extrafeatures;
    String retrunGname ,retrunGnum;
    int REQUEST_READ_CONTACTS_PERMISSION = 232036;
    int REQUEST_READ_CONTACT = 203404;
    Uri uri = Uri.parse("content://contacts");
    Intent intent = new Intent(Intent.ACTION_PICK, uri);
FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
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
        b1card = (CardView) rootView.findViewById((R.id.b1card));
        b2card = (CardView) rootView.findViewById((R.id.b2card));
        b3card = (CardView) rootView.findViewById((R.id.b3card));
        addguardian2 = (Button) rootView.findViewById((R.id.addguardian2));
        addguardian3 = (Button) rootView.findViewById((R.id.addguardian3));
        loader = new Loader(getActivity());
         dialog2 = new Dialog(getContext());
         dialog3 = new Dialog(getContext());
        dialog2.setContentView(R.layout.popup_layout_bodygurad);
         BodyGuardname2 = dialog2.findViewById(R.id.BodyGuardName) ;
         BodyGuardnumber2 = dialog2.findViewById(R.id.BodyGuardNumber) ;
        Button BodyGuardSubmit2 = dialog2.findViewById(R.id.BoduGuardSubmit);
        Button BodyGuardSelect2 = dialog2.findViewById(R.id.selectFromContacts);
        dialog3.setContentView(R.layout.popup_layout_bodygurad);
         BodyGuardname3 = dialog3.findViewById(R.id.BodyGuardName) ;
         BodyGuardnumber3 = dialog3.findViewById(R.id.BodyGuardNumber) ;
        Button BodyGuardSubmit3 = dialog3.findViewById(R.id.BoduGuardSubmit);
        Button BodyGuardSelect3 = dialog3.findViewById(R.id.selectFromContacts);


        extrafeatures = new functiontools(getActivity(),getContext());

        //if user does not select any bodybaurd as primary 1st will be selected automatically and send to the server
        autoprmrybodygaurd();
        //it loads the last saved state of switches
        LoadLastState();



        addguardian2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.show();

            }
        });
        BodyGuardSubmit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String GuardName = BodyGuardname2.getText().toString();
                String GuardNumber = BodyGuardnumber2.getText().toString();

               if ((checkConnection()))
               {
                   loader.startLoader();
                   DataRetriverFromPopup(GuardName,GuardNumber,2);
               }
               else {
                   Toast.makeText(getContext(), "Turn On Internet", Toast.LENGTH_SHORT).show();
               }

            }
        });



        BodyGuardSelect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
                } else {

                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(intent, REQUEST_READ_CONTACT);
                }
            }
        });

        BodyGuardSelect3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
                } else {
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(intent, REQUEST_READ_CONTACT);
                }
            }
        });

        addguardian3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog3.show();
            }
        });

        BodyGuardSubmit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String GuardName = BodyGuardname3.getText().toString();
                String GuardNumber = BodyGuardnumber3.getText().toString();
                if ((checkConnection()))
                {
                    loader.startLoader();
                    DataRetriverFromPopup(GuardName,GuardNumber,3);
                }
                else {
                    Toast.makeText(getContext(), "Turn On Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });



        primary1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (extrafeatures.checkConnection())
               {
                   if (primary1.isChecked())
                   {

                       primary2.setChecked(false);
                       primary3.setChecked(false);
                       PrimaryBodyGaurdSelector("true","false","false");
                       savestateforswitch(true,false,false);
                   }
               }
               else {
                   Toast.makeText(getContext(), "Turn On Internet", Toast.LENGTH_SHORT).show();
               }
            }
        });

        primary2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (extrafeatures.checkConnection())
                {
                    if (primary2.isChecked())
                    {

                        primary1.setChecked(false);
                        primary3.setChecked(false);
                        PrimaryBodyGaurdSelector("false","true","false");
                        savestateforswitch(false,true,false);
                    }
                }
                else {
                    Toast.makeText(getContext(), "Turn On Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        primary3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (extrafeatures.checkConnection())
                {
                    if (primary3.isChecked())
                    {
                        primary1.setChecked(false);
                        primary2.setChecked(false);
                        PrimaryBodyGaurdSelector("false","false","true");
                        //save instance in sharedpref
                        savestateforswitch(false,false,true);
                    }
                }
                else {
                    Toast.makeText(getContext(), "Turn On Internet", Toast.LENGTH_SHORT).show();
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

        //hiding bodyguard cards if their phonenumber are same
        if(Objects.equals(Pbnum1, Pbnum2))
        {
            addguardian2.setVisibility(View.VISIBLE);
            primary2.setVisibility(View.GONE);
            Bname2.setText("GUARDIAN 2");
            Bnum2.setText("NOT EXIST");
           //addguardian3.setVisibility(View.VISIBLE);
        }
        if (Objects.equals(Pbnum1, Pbnum3) || Objects.equals(Pbnum2, Pbnum3))
        {
            addguardian3.setVisibility(View.VISIBLE);
            primary3.setVisibility(View.GONE);
            Bname3.setText("GUARDIAN 3");
            Bnum3.setText("NOT EXIST");
        }
        // Source can be CACHE, SERVER, or DEFAULT
       // Source source = Source.CACHE;
                return rootView;
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


    void DataRetriverFromPopup(String GuardName , String  GuardNumber , int Gsno)
    {
        if (GuardName.isEmpty() || GuardNumber.isEmpty())
        {

            loader.dismissLoader();
            Toast.makeText(getContext(),"Fill All The Feilds", Toast.LENGTH_SHORT).show();
        }

        else{
            BodyGuardAddtoServerWithPopup(GuardName,GuardNumber,Gsno);
        }
    }





    void BodyGuardAddtoServerWithPopup(String Gname,String Gnumber ,int GuardSno)
    {
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


                        if (GuardSno==2)
                        {
                            bdata[3]=Gname;
                            bdata[4]=Gnumber;
                        }

                        else if (GuardSno == 3)
                        {
                            bdata[6]=Gname;
                            bdata[7]=Gnumber;
                        }

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

                                Toast.makeText(getContext(), "BodyGuard Added Restart App To Get Data", Toast.LENGTH_SHORT).show();
                                if (GuardSno==2)
                                {
                                    dialog2.dismiss();

                                }

                                else if (GuardSno == 3)
                                {
                                    dialog3.dismiss();

                                }
                            loader.dismissLoader();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_READ_CONTACT) {
            if (resultCode == -1) {
                Uri uri = intent.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                @SuppressLint("Recycle") Cursor cursor = requireActivity().getApplicationContext().getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);

                Log.d("Contact got", "ZZZ number : " + number + " , name : " + name);

                number=PhoneNumberCleaner(number);
                BodyGuardname2.setText(name);
                BodyGuardnumber2.setText(number);
                BodyGuardname3.setText(name);
                BodyGuardnumber3.setText(number);

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
    public void onNetworkChange(boolean isConnected) {

    }
}