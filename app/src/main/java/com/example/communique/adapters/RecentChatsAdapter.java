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

import com.example.communique.Chat;
import com.example.communique.R;
import com.example.communique.database.DBHelper;
import com.example.communique.helpers.Contact;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecentChatsAdapter extends RecyclerView.Adapter<RecentChatsAdapter.ViewHolder> {
    List<String> recentChatsList;
    Context context;
    ArrayList<String> onlineList;
    DBHelper dbHelper;

    public RecentChatsAdapter(List<String> recentChatsList, Context context) throws IOException {
        this.recentChatsList = recentChatsList;
        this.context = context;
        this.onlineList = Functions.stringToArray(Functions.readFile(Constants.ONLINE_USERS_FILE, context));
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public RecentChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_contact_list_item, parent, false);
        return new RecentChatsAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentChatsAdapter.ViewHolder holder, int position) {
        Contact contact = dbHelper.getContactDetails("null", recentChatsList.get(position));
        holder.userName.setText(contact.getContactName());
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra(Constants.RECIPIENT_ID, contact.getContactID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recentChatsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public TextView userName;
        public LinearLayout linearLayout;
        Button inviteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.inviteButton = itemView.findViewById(R.id.layout_invite);
            this.userImage = itemView.findViewById(R.id.layout_image);
            this.userName = itemView.findViewById(R.id.layout_name);
            linearLayout = itemView.findViewById(R.id.layout_parent);
        }
    }
}

