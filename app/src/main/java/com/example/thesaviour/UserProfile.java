package com.example.thesaviour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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

        db = FirebaseFirestore.getInstance();
        dbfetcher = db.collection("Users").document(FirebaseAuth.getInstance().getUid());
        TextView profilebackbtn= findViewById(R.id.profile_head_btn);
        profile_name = findViewById(R.id.profile_name1);
        profile_email = findViewById(R.id.profile_email);
        proile_phoneno = findViewById(R.id.profile_phoneno);
        parent_code = findViewById(R.id.parent_code);


       // loads profile from firebase
        profileloader();
        //checks whether the user is logon or not


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
                            profile_name.setText(name);
                            profile_email.setText(email);
                            proile_phoneno.setText(phonenumber);

                        }
                        else {
                            Toast.makeText(UserProfile.this, "User not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    String currentuserid()
    {
        String authuserid="";
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

    void copytoclipboard()
    {
        ClipboardManager clipboard = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            clipboard = (ClipboardManager) getSystemService(UserProfile.this.CLIPBOARD_SERVICE);
        }
        ClipData clip = ClipData.newPlainText("Parentcode",currentuserid() );
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
    }

    //it checks whether user is logged in or not

}