package com.example.communique.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.communique.R;
import com.example.communique.adapters.ContactListAdapter;
import com.example.communique.database.Database;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class ContactList extends AppCompatActivity implements View.OnClickListener {

    String TAG = "ContactList";

    boolean isSearchBarOpen = false;

    ImageView btnBack, btnSearch, btnCloseSearch;
    ProgressBar loading;
    EditText searchBar;

    Database database;
    ArrayList<String> contactsArray = new ArrayList<>();
    RecyclerView contactListView;
    ContactListAdapter contactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        loading = findViewById(R.id.layout_loading);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        btnCloseSearch = findViewById(R.id.btn_close_search);
        btnCloseSearch.setOnClickListener(this);
        contactListView = findViewById(R.id.contact_list_view);
        contactListView.setLayoutManager(new LinearLayoutManager(this));
        searchBar = findViewById(R.id.search_bar);

        database = new Database(this);
        contactListAdapter = new ContactListAdapter(contactsArray, this);
        contactListView.setAdapter(contactListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> tempArray = new ArrayList<>();
                for (String s1 : contactsArray) {
                    if(s1.toLowerCase().contains(s.toString().toLowerCase())){
                        tempArray.add(s1);
                    }
                }
                contactListAdapter.updateList(tempArray);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        Functions.showLoading(loading, true);
        if(Functions.checkFileInInternalStorage(Constants.CONTACTS_CACHE_FILE, this)){
            if(database.getContactsCount() == Functions.getContactsCount(this)){
                try {
                    ArrayList<String> tempContactsArray = Functions.stringToArray(Functions.readFileFromInternalStorage(Constants.CONTACTS_CACHE_FILE, this));
                    contactsArray.addAll(tempContactsArray);
                } catch (IOException e) {
                    Log.e(TAG, "onStart: IOException => " + e);
                    e.printStackTrace();
                }
            } else {
                getContactsFromPhone();
            }
        } else {
            getContactsFromPhone();
        }
        Functions.showLoading(loading, false);
        contactListAdapter.notifyDataSetChanged();

    }

    private void getContactsFromPhone() {
        ArrayList<String> tempContactsArray = Functions.fetchContactsFromPhone(this);
        contactsArray.addAll(tempContactsArray);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            onBackPressed();
        } else if (id == R.id.btn_search) {
            openSearchBar(true);
        } else if (id == R.id.btn_close_search) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if(isSearchBarOpen){
            openSearchBar(false);
        } else {
            super.onBackPressed();
        }
    }

    private void openSearchBar(boolean visible) {
        if(visible){
            isSearchBarOpen = true;
            btnSearch.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);
            btnCloseSearch.setVisibility(View.VISIBLE);
            searchBar.requestFocus();
            Functions.openKeyboard(this);
        } else {
            isSearchBarOpen = false;
            btnSearch.setVisibility(View.VISIBLE);
            Functions.closeKeyboard(this, this);
            searchBar.setText("");
            searchBar.setVisibility(View.GONE);
            btnCloseSearch.setVisibility(View.GONE);
        }
    }
}