package com.example.communique.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communique.R;
import com.example.communique.adapters.ChatListAdapter;
import com.example.communique.adapters.RecentChatListAdapter;
import com.example.communique.database.Database;
import com.example.communique.helpers.Message;
import com.example.communique.helpers.User;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.TreeMap;

public class Chat extends AppCompatActivity implements View.OnClickListener {

    ImageView btnBack, btnUserImage, btnOptions;
    TextView layoutRecipientName;
    EditText layoutMessageBox;
    FloatingActionButton btnSendMessage;
    RecyclerView layoutChatListView;

    Database database;
    User recipient, user;
    TreeMap<String, Message> messageList = new TreeMap<>();
    ArrayList<String> messageArray = new ArrayList<>();
    ChatListAdapter chatListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = new Database(this);
        recipient = database.getContactByID(getIntent().getExtras().getString(Constants.CONTACTID), "");
        user = database.getUserDetails();

        btnBack = findViewById(R.id.btn_back);
        btnUserImage = findViewById(R.id.btn_user_image);
        btnOptions = findViewById(R.id.btn_options);
        layoutRecipientName = findViewById(R.id.text_recipient_name);
        layoutRecipientName.setText(Functions.decryptName(recipient.getUserName()));
        layoutMessageBox = findViewById(R.id.layout_message_box);
        btnSendMessage = findViewById(R.id.btn_send);
        btnSendMessage.setOnClickListener(this);
        layoutChatListView = findViewById(R.id.layout_chat_list_view);
        layoutChatListView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        messageList.putAll(database.getMessagesFromDatabase(recipient.getUserPhone()));
        messageArray.addAll(messageList.keySet());
        chatListAdapter = new ChatListAdapter(messageList, messageArray, user.getUserPhone());
        layoutChatListView.setAdapter(chatListAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_send){
            sendMessage(layoutMessageBox.getText().toString().trim());
        }
    }

    private void sendMessage(String messageContent) {
        if(!(messageContent.isEmpty())){
            String time = String.valueOf(System.currentTimeMillis());
            Message message = new Message(
                    Functions.getUniqueID(),
                    time,
                    messageContent,
                    user.getUserPhone(),
                    recipient.getUserPhone()
            );
            messageList.put(time, message);
            messageArray.add(time);
            new Thread(() -> {database.saveMessageToDatabase(message);}).start();
            layoutMessageBox.setText("");
            chatListAdapter.notifyItemInserted(messageList.size() - 1);
            layoutChatListView.scrollToPosition(messageList.size() - 1);
        }
    }


}