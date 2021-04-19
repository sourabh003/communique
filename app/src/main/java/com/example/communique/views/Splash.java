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

    //declaration of views
    ImageView logo;

    //declaration of helpers
    Database database;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //assignment of views
        logo = findViewById(R.id.logo);

        //assignment of helpers
        database = new Database(this);
        user = database.getUserDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //function call after 1.5 sec because of fade in animation of logo
        new Handler().postDelayed(this::jumpWithDelay, 1500);
    }

    private void jumpWithDelay() {

        //checking user is null or not
        if(user != null){
            if(user.getUserPhone().isEmpty()){
                //if phone number not present go to profile setup
                startActivity(new Intent(this, ProfileSetup.class));
            } else {
                //if phone number present go to home activity
                startActivity(new Intent(this, Home.class));
            }
        } else {
            //if user is null then jump to Login Activity
            startActivity(new Intent(this, Login.class));
        }
        finish();
    }
}