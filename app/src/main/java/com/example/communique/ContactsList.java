package com.example.communique;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
import java.util.Set;

public class ContactsList extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 101;
    RecyclerView contactsRecyclerView;
    ArrayList<String> contactsList = new ArrayList<>();
    ContactListAdapter adapter;
    ImageView btnBack, btnSearch, btnCloseSearchBar;
    ProgressBar loading;
    ArrayList<String> onlineList;
    DBHelper dbHelper;
    boolean searchBarOpen = false;
    EditText searchBar;

    HashMap<String, Contact> allContactsList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        contactsRecyclerView = findViewById(R.id.contactsRecyclerview);
        contactsRecyclerView.setHasFixedSize(true);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        btnCloseSearchBar = findViewById(R.id.btn_close_search_bar);
        btnCloseSearchBar.setOnClickListener(this);
        searchBar = findViewById(R.id.text_search_contact);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NOTHING
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = String.valueOf(s);
                List<Contact> searchedList = new ArrayList<>();
                if (!(searchText.isEmpty())) {
                    for (String listItem : allContactsList.keySet()) {
                        if (listItem.contains(searchText)) {
                            searchedList.add(allContactsList.get(listItem));
                        }
                    }
                    try {
                        adapter = new ContactListAdapter(searchedList, getApplicationContext());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        adapter = new ContactListAdapter(new ArrayList<>(allContactsList.values()), getApplicationContext());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
                contactsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //NOTHING
            }
        });

        loading = findViewById(R.id.loading);

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Functions.showLoading(loading, true);
        if (dbHelper.getContactsCount() != 0) {
            getContactsFromBackup();
        } else {
            getContacts();
        }
    }

    private void getContactsFromBackup() {
        try {
            List<Contact> localContacts = dbHelper.getContacts();
            if (localContacts.size() == Functions.getContactsCount(getApplicationContext())) {
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
            } catch (Exception e) {
                Toast.makeText(this, "Exception => " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // You can directly ask for the permission.
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
        }
    }

    private void loadContactsInList(List<Contact> contactList) throws JSONException, IOException {
        new Thread(() -> {
            for (Contact contact : contactList) {
                allContactsList.put(contact.getContactName(), contact);
            }
        }).start();
        adapter = new ContactListAdapter(contactList, this);
        contactsRecyclerView.setAdapter(adapter);
        Functions.showLoading(loading, false);
        contactsRecyclerView.setVisibility(View.VISIBLE);
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
                } else {
                    Toast.makeText(this, "We cannot import Contacts without this permission", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_search:
                showSearchBar(true);
                break;
            case R.id.btn_close_search_bar:
                showSearchBar(false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (searchBarOpen) {
            showSearchBar(false);
        } else {
            finish();
        }
    }

    private void showSearchBar(boolean show) {
        if (show) {
            searchBarOpen = true;
            btnSearch.setVisibility(View.GONE);
            btnCloseSearchBar.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.VISIBLE);
        } else {
            Functions.closeKeyboard(this);
            searchBarOpen = false;
            btnSearch.setVisibility(View.VISIBLE);
            btnCloseSearchBar.setVisibility(View.GONE);
            searchBar.setVisibility(View.GONE);
        }
    }
}