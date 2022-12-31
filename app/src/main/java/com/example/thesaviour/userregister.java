package com.example.thesaviour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class userregister extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    Button rtrnbacktomain ;
    Button register;
    EditText fullname,password,phonenumber,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregister);
        rtrnbacktomain = findViewById(R.id.anon);
        register =findViewById(R.id.fregister);
        fullname=findViewById(R.id.fullname);
        phonenumber=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.usernamelogin);
        ProgressDialog pd = new ProgressDialog(userregister.this);
        rtrnbacktomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userregister.this,login.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("loading");
                pd.setMessage("Wait a second");
                pd.show();
                //reading data from textfeilds
                String Fname=fullname.getText().toString();
                String Email=email.getText().toString();
                String Phonenumber=phonenumber.getText().toString();
                String Password=password.getText().toString();

                if(Fname.isEmpty() || Email.isEmpty() || Phonenumber.isEmpty() || Password.isEmpty())
                {   pd.cancel();
                    Toast.makeText(userregister.this, "Please fill all the feilds", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            db.collection("Users").document(firebaseAuth.getInstance().getUid()).set(new usermodelstore(Fname,Phonenumber,Email));
                            pd.cancel();
                            Toast.makeText(userregister.this, "REGISTERED SUCCESSFUL", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(userregister.this , login.class));
                            Toast.makeText(userregister.this, "NOW YOU CAN LOGIN", Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.cancel();
                                    Toast.makeText(userregister.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}