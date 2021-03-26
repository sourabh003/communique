package com.example.communique.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.communique.helpers.Contact;
import com.example.communique.helpers.Message;
import com.example.communique.helpers.User;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class DBHelper extends SQLiteOpenHelper{



    public DBHelper(Context context){
        super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBQueries.CREATE_USER_TABLE);
        db.execSQL(DBQueries.CREATE_MESSAGES_TABLE);
        db.execSQL(DBQueries.CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.MESSAGES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.CONTACTS_TABLE);
        onCreate(db);
    }

    public User getCurrentUserDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        User user = null;
        Cursor result = db.rawQuery(DBQueries.GET_USER_DETAILS, null);
        if(result.getCount() == 0){
            return user;
        } else {
            while (result.moveToNext()){
                user = new User(
                        result.getString(0),
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4)
                );
            }
            result.close();
        }
        return user;
    }

    public boolean saveUserData(FirebaseUser user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.UID, user.getUid());
        contentValues.put(DBConstants.USER_NAME, user.getDisplayName());
        contentValues.put(DBConstants.USER_EMAIL, user.getEmail());
        contentValues.put(DBConstants.USER_IMAGE, user.getPhotoUrl().toString());
        contentValues.put(DBConstants.USER_PHONE, "");
        return db.insert(DBConstants.USER_TABLE, null, contentValues) != -1;
    }

    public boolean updateUserData(String id, String name, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.USER_NAME, name);
        contentValues.put(DBConstants.USER_PHONE, phone);
        db.update(DBConstants.USER_TABLE, contentValues, DBConstants.UID + " = ?", new String[]{id});
        return true;
    }

    public boolean saveMessageToDatabase(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.MESSAGE_ID, message.getMessageID());
        contentValues.put(DBConstants.MESSAGE_TO, message.getMessageTo());
        contentValues.put(DBConstants.MESSAGE_FROM, message.getMessageFrom());
        contentValues.put(DBConstants.MESSAGE_TIME, message.getMessageTime());
        contentValues.put(DBConstants.MESSAGE_CONTENT, message.getMessageContent());
        return db.insert(DBConstants.MESSAGES_TABLE, null, contentValues) != -1;
    }

    public TreeMap<String, Message> getMessages(String recipientPhone){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor messages = db.rawQuery(DBQueries.GET_RECIPIENT_MESSAGES, new String[]{recipientPhone, recipientPhone});
        if(messages.getCount() == 0){
            return new TreeMap<>();
        } else {
            TreeMap<String, Message> messageList = new TreeMap<>();
            while (messages.moveToNext()){
                Message message = new Message(
                        messages.getString(0),
                        messages.getString(3),
                        messages.getString(4),
                        messages.getString(1),
                        messages.getString(2)
                );
                messageList.put(messages.getString(3), message);
            }
            return messageList;
        }
    }

    public List<String> getRecentChats(String myPhone){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor recentChats = db.rawQuery(DBQueries.GET_RECENT_CHATS, null);
        if(recentChats.getCount() == 0){
            return new ArrayList<>();
        } else {
            List<String> contactsList = new ArrayList<>();
            while (recentChats.moveToNext()){
                String contact = recentChats.getString(recentChats.getColumnIndex(DBConstants.MESSAGE_TO));
                String contact2 = recentChats.getString(recentChats.getColumnIndex(DBConstants.MESSAGE_FROM));

                if(!(contactsList.contains(contact))){
                    contactsList.add(contact);
                }

                if(!(contactsList.contains(contact2))){
                    contactsList.add(contact2);
                }
            }
            contactsList.remove(myPhone);
            return contactsList;
        }
    }

    public Contact getContactDetails(String id, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery(DBQueries.GET_PARTICULAR_CONTACT, new String[]{id, phone});
        if(result.getCount() == 0){
            return null;
        } else {
            Contact contact = null;
            while (result.moveToNext()){
                contact = new Contact(
                        result.getString(0),
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4)
                );
            }
            return contact;
        }
    }

    public int getContactsCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor contacts = db.rawQuery(DBQueries.GET_CONTACTS, null);
        return contacts.getCount();
    }

    public boolean addContactsToDatabase(List<Contact> list){
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery(DBQueries.DELETE_ALL_CONTACTS, null).close();
        for (Contact contact : list) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBConstants.CONTACT_ID, contact.getContactID());
            contentValues.put(DBConstants.CONTACT_NAME, contact.getContactName());
            contentValues.put(DBConstants.CONTACT_EMAIL, contact.getContactEmail());
            contentValues.put(DBConstants.CONTACT_IMAGE, contact.getContactImage());
            contentValues.put(DBConstants.CONTACT_PHONE, contact.getContactPhone());
            boolean result = db.insert(DBConstants.CONTACTS_TABLE, null, contentValues) == -1;
            if(result){
                return false;
            }
        }
        return true;
    }

    public List<Contact> getContacts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor contacts = db.rawQuery(DBQueries.GET_CONTACTS, null);
        if(contacts.getCount() != 0){
            List<Contact> contactsList = new ArrayList<>();
            while (contacts.moveToNext()){
                Contact contact = new Contact(
                        contacts.getString(0),
                        contacts.getString(1),
                        contacts.getString(2),
                        contacts.getString(3),
                        contacts.getString(4)
                );
                contactsList.add(contact);
            }
            return contactsList;
        } else {
            return new ArrayList<>();
        }
    }

    public void deleteChatsFromDatabase(Contact recipientDetails){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBConstants.MESSAGES_TABLE, DBConstants.MESSAGE_TO+"=? OR "+DBConstants.MESSAGE_FROM+"=?", new String[] {recipientDetails.getContactPhone(), recipientDetails.getContactPhone()});
    }
}
