package com.example.communique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.communique.adapters.RecentChatsAdapter;
import com.example.communique.database.DBHelper;
import com.example.communique.helpers.User;
import com.example.communique.utils.CircleTransform;
import com.example.communique.utils.Constants;
import com.example.communique.utils.FirebaseUtils;
import com.example.communique.utils.Functions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener {

    String TAG = "HOME";

    ImageView btnUser, btnOptions;
    User userDetails;
    DBHelper dbHelper;

    FloatingActionButton btnAddChat;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    RecyclerView recentChatsRecyclerView;
    RecentChatsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnUser = findViewById(R.id.btn_user);
        btnUser.setOnClickListener(this);
        btnOptions = findViewById(R.id.btn_options);
        dbHelper = new DBHelper(this);
        btnOptions.setOnClickListener(this);

        btnAddChat = findViewById(R.id.btn_add_chat);
        btnAddChat.setOnClickListener(this);

        recentChatsRecyclerView = findViewById(R.id.recentChatsRecyclerView);
        recentChatsRecyclerView.setHasFixedSize(true);
        recentChatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        userDetails = dbHelper.getCurrentUserDetails();
        try {
            refreshList(dbHelper.getRecentChats(userDetails.getUserPhone()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            loadOnlineUsers();
        }).start();
        String image = userDetails.getUserImage();
        if (userDetails.getUserPhone().isEmpty()) {
            startActivity(new Intent(getApplicationContext(), UserCredentials.class));
        }

        Picasso.get().load(image).transform(new CircleTransform()).into(btnUser);
    }

    private void refreshList(List<String> recentChatsList) throws IOException {
        adapter = new RecentChatsAdapter(recentChatsList, this);
        recentChatsRecyclerView.setAdapter(adapter);
    }

    private void loadOnlineUsers() {
        databaseReference.child(FirebaseUtils.FIREBASE_USER_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    list.add(child.getKey());
                }
                if (Functions.checkFile(Constants.ONLINE_USERS_FILE, getApplicationContext())) {
                    Functions.deleteFile(Constants.ONLINE_USERS_FILE, getApplicationContext());
//                    Toast.makeText(Home.this, "File Deleted", Toast.LENGTH_SHORT).show();
                }
                try {
                    Functions.writeFile(list.toString(), Constants.ONLINE_USERS_FILE, getApplicationContext());
//                    Toast.makeText(Home.this, "New File Created", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user:
//                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                Toast.makeText(this, "User Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_options:
                showOptions();
                break;

            case R.id.btn_add_chat:
                startActivity(new Intent(this, ContactsList.class));
                break;
        }
    }

    private void showOptions() {
        Toast.makeText(this, "Options", Toast.LENGTH_SHORT).show();
    }
}