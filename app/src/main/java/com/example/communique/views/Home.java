package com.example.communique.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communique.R;
import com.example.communique.adapters.RecentChatListAdapter;
import com.example.communique.database.Database;
import com.example.communique.helpers.User;
import com.example.communique.utils.CircleTransform;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recentChatListView;
    ImageView btnProfile, btnOptions;
    FloatingActionButton btnOpenContactList;
    AlertDialog permissionDialog;

    Database database;
    User user;
    List<String> recentMessagesList = new ArrayList<>();
    HashMap<String, String> newMessagesList = new HashMap<>();
    RecentChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = new Database(this);
        user = database.getUserDetails();

        btnOpenContactList = findViewById(R.id.btn_open_contact_list);
        btnOpenContactList.setOnClickListener(this);
        btnProfile = findViewById(R.id.btn_user);
        btnProfile.setOnClickListener(this);
        btnOptions = findViewById(R.id.btn_options);
        btnOptions.setOnClickListener(this);
        recentChatListView = findViewById(R.id.layout_recent_chat_list);
        recentChatListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentChatListAdapter(recentMessagesList, newMessagesList, this);
        recentChatListView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!(user.getUserImage().isEmpty())){
            Picasso.get().load(user.getUserImage()).transform(new CircleTransform()).into(btnProfile);
        }

        recentMessagesList.clear();
        recentMessagesList.addAll(database.getRecentChats(user.getUserPhone()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_user) {

        } else if (id == R.id.btn_options) {

        } else if (id == R.id.btn_open_contact_list) {
            openContactsList();
        }
    }

    private void openContactsList() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            View view = getLayoutInflater().inflate(R.layout.layout_permission, null);
            TextView permissionTitle = view.findViewById(R.id.txt_permission_title);
            TextView permissionDescription = view.findViewById(R.id.txt_permission_description);
            Button btnContinue = view.findViewById(R.id.btn_continue);
            permissionTitle.setText(R.string.contact_permission_title);
            permissionDescription.setText(R.string.contact_permission_description);
            final AlertDialog dialogPermission = new AlertDialog.Builder(this).create();
            dialogPermission.setView(view);
            btnContinue.setOnClickListener(v -> {
                permissionDialog = dialogPermission;
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, Constants.READ_CONTACTS_PERMISSION_CODE);
            });
            dialogPermission.show();
        } else {
            startActivity(new Intent(this, ContactList.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.READ_CONTACTS_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(permissionDialog.isShowing()){
                        permissionDialog.hide();
                    }
                    startActivity(new Intent(this, ContactList.class));
                } else {
                    Toast.makeText(this, "We cannot import Contacts without this permission", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}