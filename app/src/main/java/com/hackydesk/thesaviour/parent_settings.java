package com.hackydesk.thesaviour;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class parent_settings extends AppCompatActivity {

    CardView usermode;
    CardView logout;
    CardView offLineSos;
    Switch mute;
    SharedPreferences.Editor editor;
    FirebaseUser user ;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_settings);


        user = FirebaseAuth.getInstance().getCurrentUser();

        usermode = findViewById(R.id.deviceStatus);
        logout = findViewById(R.id.logoutparent);
        mute = findViewById(R.id.MuteSwitch);
        offLineSos =findViewById(R.id.offlinesosdata);


        sharedPreferences = parent_settings.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //loads state for switch
        loadstate();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Parentchild1_Name", "null");
                editor.putString("Parentchild1_Phonenumber", "null");
                editor.putString("Parentchild1UserId", "null");
                editor.apply();

                Intent intent1 = new Intent(getApplicationContext(), firsttime.class);
                Intent intent2 = new Intent(getApplicationContext(), homie.class);

                if (user==null)
                {
                    startActivity(intent1);
                }
                else {
                    startActivity(intent2);
                }
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mute.isChecked())
                {
                    editor.putBoolean("MUTE_PARENT_ALARM",true);
                  //  Toast.makeText(parent_settings.this, "ischecked", Toast.LENGTH_SHORT).show();
                }
                else {
                    editor.putBoolean("MUTE_PARENT_ALARM",false);
                }
                editor.apply();
            }
        });

        usermode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate login then redirect further
                LoginValidator();

            }
        });

        offLineSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkData())
                {
                    Intent intent = new Intent(getApplicationContext(),offlineSosActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(parent_settings.this, "No Data Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void LoginValidator()
    {
            if (user==null)
            {
                Intent intent = new Intent(parent_settings.this,firsttime.class);
                Toast.makeText(this, "Login To Become User", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(parent_settings.this,homie.class);
                startActivity(intent);
            }
    }

    boolean checkData()
    {
        boolean result =false;
       String child1name = sharedPreferences.getString("CHILD1_NAME","null");
       String child1battery = sharedPreferences.getString("CHILD1_BATTERY_DATA","null");
        if(!child1name.contains("null") && !child1battery.contains("null"))
        {
            result = true;
        }
        return result;
    }

    void loadstate()
    {
        boolean muteswitch = sharedPreferences.getBoolean("MUTE_PARENT_ALARM",false);

        if (muteswitch)
        {
            mute.setChecked(true);
        }
        else {
            mute.setChecked(false);
        }
    }
}