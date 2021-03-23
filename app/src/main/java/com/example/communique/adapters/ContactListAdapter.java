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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communique.Chat;
import com.example.communique.R;
import com.example.communique.helpers.Contact;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    List<Contact> contactList;
    Context context;
    ArrayList<String> onlineList;

    public ContactListAdapter(List<Contact> contactList, Context context) throws JSONException, IOException {
        this.contactList = contactList;
        this.context = context;
        this.onlineList = Functions.stringToArray(Functions.readFile(Constants.ONLINE_USERS_FILE, context));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_contact_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Contact contactInfo = contactList.get(position);
        String number = contactInfo.getContactPhone();
        if (number.contains("+91")) {
            number = number.replace("+91", "");
        }
        holder.userName.setText(contactInfo.getContactName());
//            if(!(onlineList.contains(number))){
//                holder.inviteButton.setVisibility(View.VISIBLE);
//            }
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra(Constants.RECIPIENT_ID, contactInfo.getContactID());
            context.startActivity(intent);
            ((Activity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
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
