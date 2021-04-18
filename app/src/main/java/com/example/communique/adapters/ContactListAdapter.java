package com.example.communique.adapters;

import android.app.Activity;
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
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;
import com.example.communique.views.Chat;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    ArrayList<String> contactList;
    Context context;

    public ContactListAdapter(ArrayList<String> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_contact_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String contact = contactList.get(position);
        if(contact.contains(Constants.KEY_VALUE_SEPERATOR)){
            String name = contact.split(Constants.KEY_VALUE_SEPERATOR)[0];
            String id = contact.split(Constants.KEY_VALUE_SEPERATOR)[1];
            holder.textView.setText(name);
            holder.parentView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra(Constants.CONTACTID, id);
                Functions.closeKeyboard((Activity)context, context);
                context.startActivity(intent);
                ((Activity) context).finish();
            });
        }
    }

    public void updateList(ArrayList<String> list){
        contactList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactList.size();
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
        public ImageView imageView;
        public TextView textView;
        public LinearLayout parentView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.image_profile);
            this.textView = itemView.findViewById(R.id.text_name);
            this.parentView = itemView.findViewById(R.id.layout_parent_view);
        }
    }
}