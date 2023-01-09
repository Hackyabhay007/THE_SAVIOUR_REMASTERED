package com.example.thesaviour;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class parenthome extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 340;
    SharedPreferences sharedPreferences;
    TextView childname1;
    String child1userid;
    CardView child_mode;
    SharedPreferences.Editor editor;
    Context mContext;
    Button childbtn1;
    CardView Ringphone;
    CardView alertbox;
    TextView alerttext;
    CardView parent_logout;
    CardView CallBox;
    //it should be blank
    String defaultalertforalertbox = "";
    String child1phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parenthome);

        //permission to receive sms
        String[] permission = {Manifest.permission.RECEIVE_SMS};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, 239);
        }
        //context
        mContext = parenthome.this;
        child_mode = findViewById(R.id.child_mode);
        childname1 = findViewById(R.id.childname1);
        childbtn1 = findViewById(R.id.childbtn1);
        alertbox = findViewById(R.id.alertbox);
        alerttext = findViewById(R.id.alerttext);
        parent_logout = findViewById(R.id.parent_logout);
        CallBox = findViewById(R.id.callbox);
        Ringphone = findViewById(R.id.RingPhone);

        //sharedprefs
        sharedPreferences = parenthome.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //global strings
        child1userid = sharedPreferences.getString("Parentchild1UserId", "");
        child1phonenumber = sharedPreferences.getString("Parentchild1_Phonenumber", child1phonenumber);
        String weblink = "https://hackyabhay007.github.io/The_saviour_admin/??" + child1userid;
        //check any one in danger
        alertcheckfrombroadcast();
        //inflates the layout
        layoutinflator();
        childbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//
                Intent intent = new Intent(mContext, Parent_Activity.class);
                startActivity(intent);

            }
        });

        child_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, homie.class);
                startActivity(intent);
            }
        });
        parent_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Parentchild1_Name", "null");
                editor.putString("Parentchild1_Phonenumber", "null");
                editor.putString("Parentchild1UserId", "null");
                editor.apply();
                Intent intent = new Intent(mContext, firsttime.class);
                startActivity(intent);
            }
        });

        CallBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+91" + child1phonenumber));
                startActivity(callIntent);
            }
        });

        Ringphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             sendSMSMessage();
            }
        });
    }

    //make alert box animated
    void alertcheckfrombroadcast() {
        if (Objects.equals(sharedPreferences.getString("alertforparentactivity", "blank"), "19155")) {
            editor.putString("alertforparentactivity", defaultalertforalertbox);
            Toast.makeText(mContext, sharedPreferences.getString("parentchild1", "Your Child") + "Your Child Is Not Safe Opent Activity To Watch Where He is ", Toast.LENGTH_SHORT).show();
            editor.apply();
            boxanimator();
        }
    }

    void boxanimator() {
        alertbox.setVisibility(View.VISIBLE);
        alerttext.setText("Child Is not Safe");
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(600);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        alertbox.setAnimation(animation);
        alerttext.setText(sharedPreferences.getString("Parentchild1_Name", "Your Child") + " is Unsafe");
        alertbox.setBackgroundColor(Color.RED);
    }

    void layoutinflator() {
        childname1.setText(sharedPreferences.getString("Parentchild1_Name", "null"));
    }

     void sendSMSMessage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

         SmsManager smsManager = SmsManager.getDefault();
         smsManager.sendTextMessage("+91"+child1phonenumber, null, child1userid, null, null);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+91"+child1phonenumber, null, child1userid, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

        }
    }
}

