package com.example.communique.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.communique.R;
import com.example.communique.database.Database;
import com.example.communique.helpers.Message;
import com.example.communique.helpers.User;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;
import com.example.communique.views.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class RecentChatListAdapter extends RecyclerView.Adapter<RecentChatListAdapter.ViewHolder> {

    Context context;
    List<String> recentChatList;
    HashMap<String, String> newMessageList;
    Database database;

    public RecentChatListAdapter(List<String> recentChatList, HashMap<String, String> newMessageList, Context context) {
        this.context = context;
        this.recentChatList = recentChatList;
        this.newMessageList = newMessageList;
        database = new Database(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_recent_chat_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String phone = recentChatList.get(position);
        User recipient = database.getContactByID("", phone);
        holder.layoutName.setText(Functions.decryptName(recipient.getUserName()));
        if (newMessageList.containsKey(phone)){
            holder.layoutCounter.setText(newMessageList.get(phone));
            holder.layoutCounter.setVisibility(View.VISIBLE);
        }
        holder.layoutParentView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra(Constants.CONTACTID, recipient.getUid());
            context.startActivity(intent);
        });

    }

//    public void updateList(ArrayList<String> list){
//        contactList = list;
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        return recentChatList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layoutParentView;
        public TextView layoutName, layoutCounter;
        public ImageView layoutUserImage;
        public ViewHolder(View itemView) {
            super(itemView);
            layoutName = itemView.findViewById(R.id.text_name);
            layoutCounter = itemView.findViewById(R.id.text_counter);
            layoutUserImage = itemView.findViewById(R.id.image_profile);
            layoutParentView = itemView.findViewById(R.id.layout_parent_view);
        }
    }
}