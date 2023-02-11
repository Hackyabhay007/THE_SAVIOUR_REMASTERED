package com.hackydesk.thesaviour;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import components.tools;

public class userguide extends AppCompatActivity {
Button Guideclose,sharecode,copyclip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguide);
        Guideclose =findViewById(R.id.guideclose);
        sharecode =findViewById(R.id.button2);
        copyclip = findViewById(R.id.copyclipboard);

        tools t1 = new tools();
        String id = t1.currentUserid();

        Guideclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userguide.this,homie.class);
                startActivity(intent);
            }
        });

        sharecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    if (id.length()>10)
                    {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,id);
                        sendIntent.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    }


            }
        });

        copyclip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                }
                ClipData clip = ClipData.newPlainText("Parentcode",t1.currentUserid());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(userguide.this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }
}