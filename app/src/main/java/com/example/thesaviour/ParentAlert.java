package com.example.thesaviour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class ParentAlert extends AppCompatActivity {
WebView webView;
SharedPreferences sharedPreferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_alert);
        webView = findViewById(R.id.parentalertview);

        sharedPreferences = ParentAlert.this.getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        String childuserid1 = sharedPreferences.getString("parentchild1", "");
        String weblink = "https://hackyabhay007.github.io/The_saviour_admin/??"+childuserid1;
        Toast.makeText(this, weblink, Toast.LENGTH_SHORT).show();

        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(weblink);
    }
}