package com.example.communique.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.example.communique.R;
import com.example.communique.database.Database;
import com.example.communique.helpers.User;

public class Splash extends AppCompatActivity {

    ImageView logo;

    Database database;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        database = new Database(this);
        user = database.getUserDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(this::jumpWithDelay, 1500);
    }

    private void jumpWithDelay() {
        if(user != null){
            startActivity(new Intent(this, Home.class));
        } else {
            startActivity(new Intent(this, Login.class));
        }
        finish();
    }
}