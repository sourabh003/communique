package com.example.communique.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communique.R;
import com.example.communique.adapters.ChatListAdapter;
import com.example.communique.adapters.RecentChatListAdapter;
import com.example.communique.database.Database;
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

import java.util.ArrayList;
import java.util.TreeMap;

public class Chat extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "Chat";
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

    DatabaseReference firebaseRootNode = FirebaseDatabase.getInstance().getReference();
    DatabaseReference outgoingMessageNode, incomingMessageNode, newMessagesNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = new Database(this);
        recipient = database.getContactByID(getIntent().getExtras().getString(Constants.CONTACTID), "");
        user = database.getUserDetails();
        newMessagesNode = firebaseRootNode.child(FirebaseUtils.FIREBASE_RECENT_MESSAGES_NODE);
        outgoingMessageNode = firebaseRootNode.child(FirebaseUtils.USER_NODE).child(recipient.getUserPhone()).child(FirebaseUtils.MESSAGES_NODE);
        incomingMessageNode = firebaseRootNode.child(FirebaseUtils.USER_NODE).child(user.getUserPhone()).child(FirebaseUtils.MESSAGES_NODE);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnUserImage = findViewById(R.id.btn_user_image);
        btnOptions = findViewById(R.id.btn_options);
        btnOptions.setOnClickListener(this);

        layoutRecipientName = findViewById(R.id.text_recipient_name);
        layoutRecipientName.setText(Functions.decryptName(recipient.getUserName()));
        layoutMessageBox = findViewById(R.id.layout_message_box);
        btnSendMessage = findViewById(R.id.btn_send);
        btnSendMessage.setOnClickListener(this);
        layoutChatListView = findViewById(R.id.layout_chat_list_view);
        layoutChatListView.setLayoutManager(new LinearLayoutManager(this));
        chatListAdapter = new ChatListAdapter(messageList, messageArray, user.getUserPhone());
        layoutChatListView.setAdapter(chatListAdapter);

    }

    ChildEventListener incomingMessagesListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Message message = snapshot.getValue(Message.class);
            messageArray.add(message.getMessageTime());
            messageList.put(message.getMessageTime(), message);
            chatListAdapter.notifyDataSetChanged();
            new Thread(() -> {
                if(database.saveMessageToDatabase(message)){
                    incomingMessageNode.child(message.getMessageTime()).setValue(null);
                    newMessagesNode.child(user.getUserPhone()).child(recipient.getUserPhone()).child(message.getMessageTime()).setValue(message);
                }
            }).start();
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
            Log.e(TAG, "onCancelled: DatabaseError => " + error);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        if(messageArray.size() != database.getMessagesFromDatabase(recipient.getUserPhone()).size()){
            messageList.putAll(database.getMessagesFromDatabase(recipient.getUserPhone()));
            messageArray.addAll(messageList.keySet());
            chatListAdapter.notifyDataSetChanged();
        }

        incomingMessageNode.addChildEventListener(incomingMessagesListener);
    }

    @Override
    public void onBackPressed() {
        incomingMessageNode.removeEventListener(incomingMessagesListener);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_send){
            sendMessage(layoutMessageBox.getText().toString().trim());
        } else if(id == R.id.btn_back){
            onBackPressed();
        } else if(id == R.id.btn_options){
            PopupMenu menu = new PopupMenu(this, v);
            menu.setOnMenuItemClickListener(this);
            menu.inflate(R.menu.chat_menu);
            menu.show();
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
            new Thread(() -> {
                database.saveMessageToDatabase(message);
                outgoingMessageNode.child(time).setValue(message);
                newMessagesNode.child(recipient.getUserPhone()).child(user.getUserPhone()).child(time).setValue(message);
            }).start();
            layoutMessageBox.setText("");
            chatListAdapter.notifyItemInserted(messageList.size() - 1);
            layoutChatListView.scrollToPosition(messageList.size() - 1);
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.btn_clear_chat) {
            openConfirmationDialog();
            return true;
        }
        return false;
    }

    private void openConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.layout_confirmation_dialog, null);
        Button buttonCancel = dialogView.findViewById(R.id.btn_negative);
        Button buttonDelete = dialogView.findViewById(R.id.btn_positive);
        buttonDelete.setText("Delete");
        TextView dialogDescription = dialogView.findViewById(R.id.layout_dialog_description);
        dialogDescription.setText(R.string.description_delete_messages);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        buttonCancel.setOnClickListener((v) -> {
            dialog.hide();
        });
        buttonDelete.setOnClickListener((v) -> {
            Toast.makeText(this, "Messages Deleted", Toast.LENGTH_SHORT).show();
            messageList.clear();
            messageArray.clear();
            chatListAdapter.notifyDataSetChanged();
            dialog.hide();
            new Thread(() -> {
                database.deleteChatMessages(recipient.getUserPhone());
            }).start();
        });
        dialog.show();
    }
}