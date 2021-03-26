package com.example.communique;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.communique.database.DBHelper;

import org.json.JSONException;

public class Splash extends AppCompatActivity {

    ImageView imageLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageLogo = findViewById(R.id.imageLogo);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageLogo.setAnimation(animation);
        jump(animation);
    }

    private void jump(Animation animation){
        imageLogo.startAnimation(animation);
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