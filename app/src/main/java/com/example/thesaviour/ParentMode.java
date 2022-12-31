package com.example.thesaviour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ParentMode extends AppCompatActivity {
    SharedPreferences sharedPreferences ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference dbfetcher;
    SharedPreferences.Editor editor;
    Button childbtn;
    EditText childname;
    String child1phonenumber;
    String child1userid;
    String child1Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_mode);

        childbtn = findViewById(R.id.childbtn);
        childname = findViewById(R.id.childname);

        sharedPreferences = ParentMode.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();
       String child1userid = sharedPreferences.getString("Parentchild1UserId", "");
//checks parent is logged in or not
          if (child1userid.length()>9)
          {
              Toast.makeText(this, child1userid, Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(ParentMode.this, parenthome.class);
              startActivity(intent);
          }

        childbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String childstring= childname.getText().toString();
                if (childstring.isEmpty())
                {
                    Toast.makeText(ParentMode.this, "Fill The Field Please", Toast.LENGTH_SHORT).show();
                }
                else {

                    ChildDetailsFetcher(childstring);

                }
            }
        });
    }


    //it checks the userid exists if exists then add all userid data in shared db
    void ChildDetailsFetcher(String childuserid)
    {
        dbfetcher = db.collection("Users").document(childuserid);
        dbfetcher.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            child1Name = (String) documentSnapshot.get("fname");
                            child1phonenumber = (String) documentSnapshot.get("number");
                            editor.putString("Parentchild1_Name",child1Name);
                            editor.putString("Parentchild1_Phonenumber",child1phonenumber );
                            editor.putString("Parentchild1UserId",childuserid );
                            editor.apply();
                            Toast.makeText(ParentMode.this, "User Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ParentMode.this, parenthome.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getParent(), "User not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getParent(), "Something Went Wrong Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}