package com.example.communique;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communique.adapters.ChatListAdapter;
import com.example.communique.database.DBHelper;
import com.example.communique.helpers.Contact;
import com.example.communique.helpers.Message;
import com.example.communique.helpers.User;
import com.example.communique.utils.Constants;
import com.example.communique.utils.FirebaseUtils;
import com.example.communique.utils.Functions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

public class Chat extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    String TAG = "Chat";

    ImageView userProfileImage, btnMenuOptions, btnBack;
    TextView userName;
    FloatingActionButton buttonSend;
    EditText textMessage;

    DBHelper dbHelper;
    User userDetails;
    String userPhone = "";
    String recipientPhone = "";
    Contact recipientDetails;

    DatabaseReference usersNode = FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.FIREBASE_USER_NODE);
    DatabaseReference userChatNode;
    DatabaseReference recipientChatNode;

    RecyclerView chatListView;
    ChatListAdapter adapter;

    TreeMap<String, Message> messageList = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userProfileImage = findViewById(R.id.image_user);
        userName = findViewById(R.id.recipientName);
        textMessage = findViewById(R.id.textMessage);
        buttonSend = findViewById(R.id.btnSendMessage);
        buttonSend.setOnClickListener(this);
        chatListView = findViewById(R.id.chatListView);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatListView.setLayoutManager(layoutManager);

        btnMenuOptions = findViewById(R.id.btn_options);
        btnMenuOptions.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        userDetails = dbHelper.getCurrentUserDetails();

        userPhone = userDetails.getUserPhone();
        userChatNode = usersNode.child(userPhone).child(FirebaseUtils.FIREBASE_CHAT_NODE);
        recipientDetails = dbHelper.getContactDetails(getIntent().getExtras().getString(Constants.RECIPIENT_ID), "null");
        recipientPhone = recipientDetails.getContactPhone();
        userName.setText(recipientDetails.getContactName());
        recipientChatNode = usersNode.child(recipientPhone).child(FirebaseUtils.FIREBASE_CHAT_NODE);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendMessage:
                sendMessage(textMessage.getText().toString().trim());
                break;

            case R.id.btn_options:
                PopupMenu menu = new PopupMenu(this, v);
                menu.setOnMenuItemClickListener(this);
                menu.inflate(R.menu.chat_menu);
                menu.show();
                break;

            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        messageList.putAll(dbHelper.getMessages(recipientPhone));
        refreshChatList(true);

        userChatNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messageList.put(message.getMessageTime(), message);
                refreshChatList(false);
                if (dbHelper.saveMessageToDatabase(message)) {
                    userChatNode.setValue(null);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void refreshChatList(boolean startUp) {
        adapter = new ChatListAdapter(messageList, userPhone, getApplicationContext(), startUp);
        chatListView.setAdapter(adapter);
        chatListView.scrollToPosition(messageList.size() - 1);
    }

    private void sendMessage(String message) {
        if (!(message.isEmpty())) {
            saveMessageInDataabses(message);
        }
    }

    private void saveMessageInDataabses(String messageContent) {
        Message message = new Message(
                Functions.getUniqueID(),
                String.valueOf(System.currentTimeMillis()),
                messageContent,
                userPhone,
                recipientPhone
        );
        messageList.put(message.getMessageTime(), message);
        refreshChatList(false);
        textMessage.setText("");
        new Thread(() -> {
            dbHelper.saveMessageToDatabase(message);
            recipientChatNode.child(message.getMessageTime()).setValue(message);
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_clear_chat:
                clearChats();
                return true;
            default:
                return false;
        }
    }

    private void clearChats() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_clear_chat, null);
        Button buttonCancel = dialogView.findViewById(R.id.btn_cancel);
        Button buttonDelete = dialogView.findViewById(R.id.btn_delete);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener((v) -> {
            dialog.hide();
        });

        buttonDelete.setOnClickListener((v) -> {
            Toast.makeText(this, "Messages Deleted", Toast.LENGTH_SHORT).show();
            messageList.clear();
            refreshChatList(false);
            dialog.hide();
            new Thread(() -> {
                dbHelper.deleteChatsFromDatabase(recipientDetails);
            }).start();
        });
        dialog.show();

    }
}