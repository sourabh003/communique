package com.example.communique.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communique.R;
import com.example.communique.helpers.Message;

import java.util.ArrayList;
import java.util.TreeMap;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    TreeMap<String, Message> messageList;
    Context context;
    String me;
    ArrayList<String> list = new ArrayList<>();
    boolean startUp;

    public ChatListAdapter(TreeMap<String, Message> messageList, String me, Context context, boolean startUp) {
        this.messageList = messageList;
        this.context = context;
        this.me = me;
        this.startUp = startUp;
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
        Message message = messageList.get(list.get(position));
        Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_up);
        MediaPlayer mp = MediaPlayer.create(context, R.raw.ping_sound_effect);

        if (message.getMessageTo().equals(me)) {
            holder.recipientMessage.setVisibility(View.VISIBLE);
            holder.recipientMessage.setText(message.getMessageContent());
            if(position == messageList.size() - 1){
                if(!startUp){
                    holder.recipientMessage.startAnimation(fadeInAnimation);
                    mp.start();
                    mp.release();
                }
            }
        } else {
            holder.senderMessage.setVisibility(View.VISIBLE);
            holder.senderMessage.setText(message.getMessageContent());
            if(position == messageList.size() - 1){
                holder.senderMessage.startAnimation(fadeInAnimation);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
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
