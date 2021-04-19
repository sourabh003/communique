package com.example.communique.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communique.R;
import com.example.communique.database.Database;
import com.example.communique.helpers.User;
import com.example.communique.utils.CircleTransform;
import com.example.communique.utils.Constants;
import com.example.communique.utils.FirebaseUtils;
import com.example.communique.utils.Functions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ProfileSetup extends AppCompatActivity implements View.OnClickListener {

    ImageView layoutUserImage;
    TextView layoutUserEmail;
    EditText layoutUserName, layoutUserPhone;
    Button buttonSave;

    Database database;
    User user;
    DatabaseReference userNode = FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USER_NODE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        database = new Database(this);
        user = database.getUserDetails();

        layoutUserEmail = findViewById(R.id.layout_user_email);
        layoutUserEmail.setText(user.getUserEmail());
        layoutUserImage = findViewById(R.id.layout_user_image);
        Picasso.get().load(user.getUserImage()).transform(new CircleTransform()).into(layoutUserImage);
        layoutUserName = findViewById(R.id.layout_user_name);
        layoutUserName.setText(user.getUserName());
        layoutUserPhone = findViewById(R.id.layout_user_phone);
        if(!user.getUserPhone().isEmpty()){
            layoutUserPhone.setText(user.getUserPhone());
        }
        buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_save){
            Functions.closeKeyboard(this, this);
            saveDetails(layoutUserName.getText().toString().trim(), layoutUserPhone.getText().toString().trim());
        }
    }

    private void saveDetails(String name, String phone) {
        if(!(name.isEmpty()) && !(phone.isEmpty())){
            if(phone.length() == 10){
                phone = Constants.COUNTRY_CODE_INDIA + phone;
                String finalPhone = phone;
                User updatedUser = new User(
                        user.getUid(),
                        name,
                        user.getUserEmail(),
                        user.getUserImage(),
                        finalPhone
                );
                new Thread(() -> {
                    database.updateUserData(user.getUid(), name, finalPhone, user.getUserImage());
                    userNode.child(finalPhone).child(FirebaseUtils.USER_DETAILS_NODE).setValue(updatedUser);
                }).start();
                startActivity(new Intent(this, Home.class));
                finishAffinity();
            } else {
                Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Field's Empty!", Toast.LENGTH_SHORT).show();
        }
    }
}