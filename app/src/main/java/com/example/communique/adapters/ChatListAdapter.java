package com.example.communique.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.communique.R;
import com.example.communique.database.Database;
import com.example.communique.helpers.Message;
import com.example.communique.helpers.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    String myPhone;
    TreeMap<String, Message> messageList;
    ArrayList<String> messageArray;

    public ChatListAdapter(TreeMap<String, Message> messageList, ArrayList<String> messageArray, String myPhone) {
        this.myPhone = myPhone;
        this.messageList = messageList;
        this.messageArray = messageArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_chat_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String messageTime = messageArray.get(position);
        Message message = messageList.get(messageTime);
        if(message.getMessageTo().equals(myPhone)){
            holder.layoutIncomingMessage.setText(message.getMessageContent());
            holder.layoutIncomingMessage.setVisibility(View.VISIBLE);
        } else {
            holder.layoutOutgoingMessage.setText(message.getMessageContent());
            holder.layoutOutgoingMessage.setVisibility(View.VISIBLE);
        }
    }

//    public void updateList(ArrayList<String> list){
//        contactList = list;
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        return messageArray.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView layoutIncomingMessage, layoutOutgoingMessage;
        public ViewHolder(View itemView) {
            super(itemView);
            layoutIncomingMessage = itemView.findViewById(R.id.layout_incoming_message_text);
            layoutOutgoingMessage = itemView.findViewById(R.id.layout_outgoing_message_text);
        }
    }
}