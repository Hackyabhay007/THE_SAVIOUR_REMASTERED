package com.hackydesk.thesaviour;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {



    SharedPreferences sharedPreferences ;

    int DELAY = 2000;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getApplicationContext().getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        String child1userid = sharedPreferences.getString("Parentchild1UserId", "");


        if(child1userid.length()>9 )
        {

            Intent intent2 = new Intent(MainActivity.this, AllPermissionAtOnce.class);
            startActivity(intent2);
        }
        else
        {
          if(ValidteSettings())
          {

              Intent intent = new Intent(MainActivity.this, AllPermissionAtOnce.class);
              startActivity(intent);
          }
          else {
              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                  @Override
                  public void run() {

                      if (user==null)
                      {
                          Intent intent = new Intent(MainActivity.this, firsttime.class);
                          startActivity(intent);

                      }



                      else
                      {
                          Intent intent3 = new Intent(MainActivity.this, AllPermissionAtOnce.class);
                          startActivity(intent3);
                      }


                  }
              }, DELAY);
              // Check if user is signed in (non-null) and update UI accordingly.

          }
        }




    }
    boolean ValidteSettings()
    {
        boolean result=false;
        if (sharedPreferences.getBoolean("SKIP_SPLASH",false))
        {
           result = true;
        }
        return result;
    }

}