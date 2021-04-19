package com.example.communique.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Login";
    ProgressBar loading;
    EditText layoutName, layoutEmail, layoutPhone;
    Button buttonGoogle, buttonSignUp;

    Database database;
    DatabaseReference firebaseUsersNode = FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USER_NODE);

    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        Functions.closeKeyboard(this, this);
        if(viewID == R.id.button_google_sign_up){
            googleSignIn();
        } else if(viewID == R.id.button_sign_up){
            localSignup(layoutName.getText().toString().trim(), layoutEmail.getText().toString().trim(), layoutPhone.getText().toString().trim());
        }
    }

    private void localSignup(String name, String email, String phone) {
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

    private void googleSignIn() {
        Functions.showLoading(loading, true);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Sign in failed!", e);
                Toast.makeText(this, "Sign in Failed! -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserData(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(Login.this, "Sign in failed!", Toast.LENGTH_SHORT).show();
                        Functions.showLoading(loading, false);
                    }
                });
    }

    private void saveUserData(FirebaseUser user) {
        new Thread(() -> {
            database.saveUserDetails(user, null);
        }).start();
        startActivity(new Intent(this, ProfileSetup.class));
        finish();
    }
}