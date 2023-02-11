package com.hackydesk.thesaviour;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import components.Loader;
import components.functiontools;

public class ParentMode extends AppCompatActivity {
    SharedPreferences sharedPreferences ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference dbfetcher;
    SharedPreferences.Editor editor;
    Button childbtn;
    EditText ChildName,parentName,Guardiannumber;
    String child1phonenumber;
    String child1userid;
    String verifieduserid="";
    String child1Name;
    private Loader loader;
    functiontools extrafunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_mode);

        loader = new Loader(ParentMode.this);

        childbtn = findViewById(R.id.childbtn);
        ChildName = findViewById(R.id.childname);
        parentName = findViewById(R.id.guardianname);
        sharedPreferences = ParentMode.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();
        Guardiannumber = findViewById(R.id.guardiannumber);

        extrafunctions = new functiontools(ParentMode.this,getApplicationContext());
       String child1userid = sharedPreferences.getString("Parentchild1UserId", "");
//checks parent is logged in or not
          if (child1userid.length()>9)
          {
              //Toast.makeText(this, child1userid, Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(ParentMode.this, AllPermissionAtOnce.class);
              startActivity(intent);
          }
        childbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String childstring= ChildName.getText().toString();
                if (childstring.isEmpty() || Guardiannumber.getText().toString().isEmpty() || parentName.getText().toString().isEmpty())
                {
                    Toast.makeText(ParentMode.this, "Fill The Fields", Toast.LENGTH_SHORT).show();
                }

                else if(Guardiannumber.getText().toString().length()<10)
                {
                    Toast.makeText(ParentMode.this, "Phone Number Should Contain 10 Digits", Toast.LENGTH_SHORT).show();
                }
                else {
                    loader.startLoader();
                    ChildDetailsFetcher(childstring);

                }
            }
        });
    }

    //it checks the userid exists if exists then add all userid data in shared db
    void ChildDetailsFetcher(String childuserid)
    {
        dbfetcher = db.collection("Users").document(childuserid);
        dbfetcher.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            verifieduserid = childuserid;
                            child1Name = (String) documentSnapshot.get("fname");
                            child1phonenumber = (String) documentSnapshot.get("number");
                            String email = (String) documentSnapshot.get("email");
                            editor.putString("Parentchild1_Name",child1Name);
                            editor.putString("Parentchild1_Phonenumber",child1phonenumber );
                            editor.putString("Parentchild1UserId",childuserid );
                            editor.apply();
                            Toast.makeText(ParentMode.this, child1Name.toLowerCase(Locale.ROOT)+" Added", Toast.LENGTH_SHORT).show();
                           //generates token and submit to the server
                            tokengenerator();
                          //  loader.dismissLoader();

                        }
                        else {
                            loader.dismissLoader();
                            Toast.makeText(getApplicationContext(), "Guardian Code Not Valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loader.dismissLoader();
                        Toast.makeText(getApplicationContext(), "Something Went Wrong Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }});
    }


    void submitDatatoServer(String token)
    {
        if(Permissionscheck())
        {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();

            data.put("parentname", parentName.getText().toString());
            data.put("token", token);
            data.put("childid",ChildName.getText().toString());
            data.put("parentnumber",Guardiannumber.getText().toString());

            db.collection("ParentsData").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                            loader.dismissLoader();
                            //extrafunctions.sendOnlineMesageToparent("Parent Login Detected", parentName.getText().toString() + "Is Connected As Guardian");
                            Intent intent = new Intent(ParentMode.this, AllPermissionAtOnce.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error adding document", e);
                            loader.dismissLoader();
                        }
                    });
        }



    }

//it generates token and sent data to server by call data submit to server method
    void tokengenerator()
    {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("firebase messaging", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        submitDatatoServer(token);
                        Log.d("user token", token);
                    }
                });
    }
boolean result = false;
    boolean Permissionscheck()
    {
        functiontools extrafeatures = new functiontools(ParentMode.this,getApplicationContext());

        if (extrafeatures.checkConnection())
        {


            if (!extrafeatures.checkGpsStatus())
            {
                loader.dismissLoader();
                extrafeatures.requestGPSPermission();
            }
            else {
                result =  true;
            }
        }
        else {
            loader.dismissLoader();
            AlertDialog.Builder builder = new AlertDialog.Builder(ParentMode.this);
            builder.setMessage("Turn On Internet");
            builder.setIcon(R.drawable.mainlog_transparent_bg);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something
                }
            });
            builder.show();
        }


        return result;
    }


}