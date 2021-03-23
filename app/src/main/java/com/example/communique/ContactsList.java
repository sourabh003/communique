package com.example.communique;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.communique.adapters.ContactListAdapter;
import com.example.communique.database.DBHelper;
import com.example.communique.helpers.Contact;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsList extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 101;
    RecyclerView contactsRecyclerView;
    ArrayList<String> contactsList = new ArrayList<>();
    ContactListAdapter adapter;
    ImageView btnBack;
    ProgressBar loading;
    ArrayList<String> onlineList;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        contactsRecyclerView = findViewById(R.id.contactsRecyclerview);
        contactsRecyclerView.setHasFixedSize(true);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        loading = findViewById(R.id.loading);

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Functions.showLoading(loading, true);
        if(dbHelper.getContactsCount() != 0){
            getContactsFromBackup();
        } else {
            getContacts();
        }
        Functions.showLoading(loading, false);
    }

    private void getContactsFromBackup(){
        try {
            List<Contact> localContacts = dbHelper.getContacts();
            if(localContacts.size() == Functions.getContactsCount(getApplicationContext())){
                loadContactsInList(localContacts);
            } else {
                getContacts();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getContacts() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            try {
                loadContactsInList(Functions.getContactsFomPhone(getApplicationContext()));
            } catch (Exception e){
                Toast.makeText(this, "Exception => " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // You can directly ask for the permission.
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
        }
    }

    private void loadContactsInList(List<Contact> contactList) throws JSONException, IOException {
        adapter = new ContactListAdapter(contactList, this);
        contactsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    getContacts();
                }  else {
                    Toast.makeText(this, "We cannot import Contacts without this permission", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_back){
            finish();
        }
    }
}