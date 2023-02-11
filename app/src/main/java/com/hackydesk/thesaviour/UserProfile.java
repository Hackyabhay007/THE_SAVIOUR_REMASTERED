package com.hackydesk.thesaviour;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfile extends AppCompatActivity {
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    DocumentReference dbfetcher;
    TextView profile_name;
    TextView profile_email;
    TextView proile_phoneno;
    TextView parent_code;
    Button shareguardiancode;
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
        shareguardiancode = findViewById(R.id.share_guardiancode);



//loads profile from local db saved at homie.java from server
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

        shareguardiancode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCode();
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

    void shareCode()
    {
        String id = currentuserid();

        if (id.length()>10)
        {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,id);
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }

        else {
            Toast.makeText(this, "try After Restarting App", Toast.LENGTH_SHORT).show();
        }


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