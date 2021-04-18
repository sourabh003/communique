package com.example.communique.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import com.example.communique.utils.Functions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.TreeMap;

public class Chat extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

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

    @Override
    protected void onStart() {
        super.onStart();

        if(messageArray.size() != database.getMessagesFromDatabase(recipient.getUserPhone()).size()){
            messageList.putAll(database.getMessagesFromDatabase(recipient.getUserPhone()));
            messageArray.addAll(messageList.keySet());
            chatListAdapter.notifyDataSetChanged();
        }
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
            new Thread(() -> {database.saveMessageToDatabase(message);}).start();
            layoutMessageBox.setText("");
            chatListAdapter.notifyItemInserted(messageList.size() - 1);
            layoutChatListView.scrollToPosition(messageList.size() - 1);
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_clear_chat:
                openConfirmationDialog();
                return true;
            default:
                return false;
        }
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