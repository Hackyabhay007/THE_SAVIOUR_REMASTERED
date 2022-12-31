package com.example.thesaviour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class firsttime extends AppCompatActivity {
Button login;
Button register;
Button parentmode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firsttime);

        //must methods
        ActiveLoginCheck();

        login =findViewById(R.id.LOGIN);
        register =findViewById(R.id.Parentmode);
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

    //it checks whether user is logged in or not
    void ActiveLoginCheck()
    {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(firsttime.this, homie.class);
            startActivity(intent);
        }

    }
}