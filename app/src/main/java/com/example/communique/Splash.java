package com.example.communique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.communique.database.DBHelper;
import com.example.communique.helpers.User;
import com.example.communique.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash extends AppCompatActivity {

    private static final String TAG = "SPLASH";
    ImageView imageLogo;
    DBHelper dbHelper;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.FIREBASE_USER_NODE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageLogo = findViewById(R.id.imageLogo);
        dbHelper = new DBHelper(this);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageLogo.setAnimation(animation);
        jump(animation);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(!(dbHelper.getCurrentUserDetails().getUserPhone().isEmpty())){
//            new Thread(this::loadNewMessages).start();
//        }
//    }
//
//    private void loadNewMessages() {
//        User user = dbHelper.getCurrentUserDetails();
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                dbHelper.insertRecentMessages(user, snapshot);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "Error in Splash Activity : " + error.getMessage());
//            }
//        });
//    }

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