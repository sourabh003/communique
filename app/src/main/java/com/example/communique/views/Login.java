package com.example.communique.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.communique.R;
import com.example.communique.database.Database;
import com.example.communique.helpers.User;
import com.example.communique.utils.Constants;
import com.example.communique.utils.FirebaseUtils;
import com.example.communique.utils.Functions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements View.OnClickListener {

    ProgressBar loading;
    EditText layoutName, layoutEmail, layoutPhone;
    Button buttonGoogle, buttonSignUp;

    Database database;
    DatabaseReference firebaseUsersNode = FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USER_NODE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Views
        loading = findViewById(R.id.layout_loading);
        layoutName = findViewById(R.id.layout_name_input);
        layoutEmail = findViewById(R.id.layout_email_input);
        layoutPhone = findViewById(R.id.layout_phone_input);
        buttonGoogle = findViewById(R.id.button_google_sign_up);
        buttonGoogle.setOnClickListener(this);
        buttonSignUp = findViewById(R.id.button_sign_up);
        buttonSignUp.setOnClickListener(this);

        database = new Database(this);
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        Functions.closeKeyboard(this, this);
        if(viewID == R.id.button_google_sign_up){
            googleSignIn();
        } else if(viewID == R.id.button_sign_up){
            signUp(layoutName.getText().toString().trim(), layoutEmail.getText().toString().trim(), layoutPhone.getText().toString().trim());
        }
    }

    private void googleSignIn() {
        Functions.showLoading(loading, true);
    }

    private void signUp(String name, String email, String phone) {
        Functions.showLoading(loading, true);
        if(!(name.isEmpty()) && !(phone.isEmpty())){
            if(phone.length() == 10){
                phone = Constants.COUNTRY_CODE_INDIA + phone;
                User user = new User(
                        Functions.getUniqueID(),
                        name,
                        email,
                        "",
                        phone
                );
                new Thread(() -> {
                    database.saveUserDetails(null, user);
                    firebaseUsersNode.child(user.getUserPhone()).child(FirebaseUtils.USER_DETAILS_NODE).setValue(user);
                }).start();
                //To be replaced with ProfileSetup Activity
//            startActivity(new Intent(this, ProfileSetup.class));
                startActivity(new Intent(this, Home.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid Phone Number!", Toast.LENGTH_SHORT).show();
                Functions.showLoading(loading, false);
            }
        } else {
            Toast.makeText(this, "Fields Empty!", Toast.LENGTH_SHORT).show();
            Functions.showLoading(loading, false);
        }
    }
}