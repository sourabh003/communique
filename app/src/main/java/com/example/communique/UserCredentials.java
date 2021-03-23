package com.example.communique;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communique.database.DBHelper;
import com.example.communique.utils.CircleTransform;
import com.example.communique.utils.Constants;
import com.example.communique.utils.FirebaseUtils;
import com.example.communique.utils.Functions;
import com.example.communique.helpers.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class UserCredentials extends AppCompatActivity implements View.OnClickListener {

    EditText editTextPhone, editTextName;
    TextView textViewEmail;
    ImageView imageViewProfileImage;
    Button buttonSave;

    User userDetails;
    DBHelper dbHelper;
    String userID;
    ProgressBar loading;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_credentials);

        editTextPhone = findViewById(R.id.text_phone);
        editTextName = findViewById(R.id.text_name);
        textViewEmail = findViewById(R.id.text_email);
        imageViewProfileImage = findViewById(R.id.image_user);
        buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(this);
        loading = findViewById(R.id.loading);

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userDetails = dbHelper.getCurrentUserDetails();
        userID = userDetails.getUid();
        Picasso.get().load(userDetails.getUserImage()).transform(new CircleTransform()).into(imageViewProfileImage);
        textViewEmail.setText(userDetails.getUserEmail());
        editTextName.setText(userDetails.getUserName());
        editTextPhone.setText(userDetails.getUserPhone());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "We cannot proceed without details!", Toast.LENGTH_SHORT).show();
        finishAffinity();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_save) {
            saveUserCredentials(editTextName.getText().toString().trim(), editTextPhone.getText().toString().trim());
        }
    }

    private void saveUserCredentials(String name, String phone) {
        Functions.showLoading(loading, true);
        if (!(name.isEmpty()) && !(phone.isEmpty())) {
            if (phone.length() < 10) {
                Toast.makeText(this, "Number Invalid!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Home.class));
                finishAffinity();
                new Thread(() -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String updatedPhone = Constants.COUNTRY_CODE_INDIA + phone;
                    databaseReference.child(FirebaseUtils.FIREBASE_USER_NODE).child(updatedPhone).setValue(new User(user.getUid(), name, user.getEmail(), user.getPhotoUrl().toString(), updatedPhone));
                    dbHelper.updateUserData(userID, name, updatedPhone);
                }).start();
            }
        } else {
            Toast.makeText(this, "Fields can't be empty!", Toast.LENGTH_SHORT).show();
        }
    }
}