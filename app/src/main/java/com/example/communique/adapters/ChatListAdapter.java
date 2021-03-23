package com.example.communique.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.communique.Chat;
import com.example.communique.R;
import com.example.communique.helpers.Message;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;
import com.google.firebase.database.core.utilities.Tree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    TreeMap<String, Message> messageList;
    Context context;
    String me;
    ArrayList<String> list = new ArrayList<>();

    public ChatListAdapter(TreeMap<String, Message> messageList, String me, Context context) {
        this.messageList = messageList;
        this.context = context;
        this.me = me;
        list.addAll(messageList.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_chat_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Message message = messageList.get(list.get(position));
            if(message.getMessageTo().equals(me)){
                holder.recipientMessage.setVisibility(View.VISIBLE);
                holder.recipientMessage.setText(message.getMessageContent());
            } else {
                holder.senderMessage.setVisibility(View.VISIBLE);
                holder.senderMessage.setText(message.getMessageContent());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessage;
        public TextView recipientMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            this.senderMessage = itemView.findViewById(R.id.senderMessage);
            this.recipientMessage = itemView.findViewById(R.id.recipientMessage);
        }
    }
}
