package com.example.thesaviour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class UserProfile extends AppCompatActivity {
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    DocumentReference dbfetcher;
    TextView profile_name;
    TextView profile_email;
    TextView proile_phoneno;
    TextView parent_code;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        sharedPreferences = UserProfile.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        db = FirebaseFirestore.getInstance();
        dbfetcher = db.collection("Users").document(FirebaseAuth.getInstance().getUid());
        TextView profilebackbtn= findViewById(R.id.profile_head_btn);
        profile_name = findViewById(R.id.profile_name1);
        profile_email = findViewById(R.id.profile_email);
        proile_phoneno = findViewById(R.id.profile_phoneno);
        parent_code = findViewById(R.id.parent_code);


       // loads profile from firebase
        profileloader();
//loads profile from local db
        ProfileInflator();
        //checks whether the user is login or not
        profilebackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, homie.class);
                startActivity(intent);
            }
        });
        parent_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //copies current user id to clipboard
                copytoclipboard();
            }
        });
    }

    //fetch data from database
    void profileloader()
    {
        dbfetcher.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            String name = (String) documentSnapshot.get("fname");
                            String email = (String) documentSnapshot.get("email");
                            String phonenumber = (String) documentSnapshot.get("number");
                            //it inflates online
                            profile_name.setText(name);
                            profile_email.setText(email);
                            proile_phoneno.setText(phonenumber);
                            //it stores first time local db

//                            -------------------
                            editor.putString("profile_name",name);
                            editor.putString("profile_email",email );
                            editor.putString("profile_phnnumber",phonenumber );
                            editor.apply();

                        }
                        else {
                            Toast.makeText(UserProfile.this, "User not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, "Something Went Wrong Or Internet Off", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    String currentuserid()
    {
        String authuserid="";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            authuserid = user.getUid();
            editor.putString("profile_parent_code",authuserid );
            editor.apply();
        }
        return authuserid;
    }

    void copytoclipboard()
    {
        ClipboardManager clipboard = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            clipboard = (ClipboardManager) getSystemService(UserProfile.this.CLIPBOARD_SERVICE);
        }
        ClipData clip = ClipData.newPlainText("Parentcode", sharedPreferences.getString("profile_parent_code", "null"));
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
    }

    //it checks whether user is logged in or not

    //inflate layout from local db
    void ProfileInflator()
    {
        currentuserid();
        profile_name.setText( sharedPreferences.getString("profile_name", "null"));
       profile_email.setText(sharedPreferences.getString("profile_email", "null"));
       proile_phoneno.setText( sharedPreferences.getString("profile_phnnumber", "null"));

    }

}