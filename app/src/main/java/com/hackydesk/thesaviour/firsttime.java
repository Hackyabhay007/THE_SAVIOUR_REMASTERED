package com.hackydesk.thesaviour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class firsttime extends AppCompatActivity {
Button login;
Button register;
Button parentmode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firsttime);

        login =findViewById(R.id.LOGIN);
        register =findViewById(R.id.register);
        parentmode = findViewById(R.id.Parentmode);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(firsttime.this, login.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(firsttime.this, userregister.class);
                startActivity(intent);
            }
        });
        parentmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(firsttime.this, ParentMode.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }

}