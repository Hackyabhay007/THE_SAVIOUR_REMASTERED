package com.hackydesk.thesaviour;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button button = findViewById(R.id.rtrnbacktomain2);
        final Button anon = findViewById(R.id.anon);
        final Button login_btn = findViewById(R.id.login_btn);
        final TextView Usernamelogin = findViewById(R.id.usernamelogin);
        final TextView password = findViewById(R.id.password);
        ProgressDialog pd = new ProgressDialog(login.this);
        //firebase authentication

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this,userregister.class);
                startActivity(intent);
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Username =Usernamelogin.getText().toString();
                //Toast.makeText(login.this, Username, Toast.LENGTH_SHORT).show();
                String Password =password.getText().toString();
                pd.setMessage("Loading...");
                pd.show();
                if(Username.isEmpty() || Password.isEmpty())
                {
                    pd.cancel();
                    Toast.makeText(login.this, "FILED IS EMPETY", Toast.LENGTH_SHORT).show();
                }
                else {
                        firebaseAuth.signInWithEmailAndPassword(Username,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                pd.cancel();
                                Toast.makeText(login.this, "Login Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login.this,AllPermissionAtOnce.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.cancel();
                                Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
    }


}
