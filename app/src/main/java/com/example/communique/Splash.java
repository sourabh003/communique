package com.example.communique;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.communique.database.DBHelper;

import org.json.JSONException;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        jump();
    }

    private void jump(){
        DBHelper dbHelper = new DBHelper(this);
        Intent intent;
        if(dbHelper.getCurrentUserDetails() != null){
            intent = new Intent(this, Home.class);
        } else {
            intent = new Intent(this, Welcome.class);
        }
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            startActivity(intent);
            finishAffinity();
        }, 1500);
    }
}