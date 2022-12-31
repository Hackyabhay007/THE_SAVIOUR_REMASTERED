package com.example.thesaviour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class bodygaurdactivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    EditText bnumber;
    EditText bname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodygaurdactivity);

        //checks user is login or not
        ActiveLoginCheck();

         bname =  findViewById(R.id.bname);
         bnumber = findViewById(R.id.bnumber);
        Button Addprotector = findViewById(R.id.addprotector);
        Button  homie = findViewById(R.id.bcktosetting);
        ProgressDialog pd = new ProgressDialog(this);

        String bname1 = bname.getText().toString();
        String bnum1 = bnumber.getText().toString();

        homie.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           startActivity(new Intent(bodygaurdactivity.this,homie.class));
       }
   });
        Addprotector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (bname1.isEmpty() || bnum1.isEmpty())
               {
                   Toast.makeText(bodygaurdactivity.this, "Fill All feilds", Toast.LENGTH_SHORT).show();
               }
               else
               {
                   Map<String, String> bdata = new HashMap<>();
                   bdata.put("bodygaurddata", "aman gupta,7477066373,true,ganga gupta,7470391011,false,Hacky,7477066373,false");

                   db.collection("Users").document(firebaseAuth.getInstance().getUid())
                           .set(bdata, SetOptions.merge())
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(bodygaurdactivity.this, "SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(bodygaurdactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           });

                   // Toast.makeText(bodygaurdactivity.this, "BodyGaurd Added Successfully", Toast.LENGTH_SHORT).show();
               }
            }
        });

//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(bodygaurdactivity.this, homie.class);
//            startActivity(intent);
//        }
//        else{
//            Intent intent = new Intent(bodygaurdactivity.this, login.class);
//            startActivity(intent);
//        }
    }

    //it checks whether user is logged in or not
    void ActiveLoginCheck()
    {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(bodygaurdactivity.this, firsttime.class);
            startActivity(intent);
        }

    }
}