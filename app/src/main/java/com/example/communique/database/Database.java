package com.example.communique.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.communique.helpers.Contact;
import com.example.communique.helpers.Message;
import com.example.communique.helpers.User;
import com.example.communique.utils.Constants;
import com.example.communique.utils.Functions;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Database extends SQLiteOpenHelper {

    String TAG = "database";

    public Database(Context context) {
        super(context, DatabaseConfigs.DATABASE_NAME, null, DatabaseConfigs.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConfigs.CREATE_USER_TABLE);
        db.execSQL(DatabaseConfigs.CREATE_MESSAGES_TABLE);
        db.execSQL(DatabaseConfigs.CREATE_CONTACTS_TABLE);
        db.execSQL(DatabaseConfigs.CREATE_RECENT_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConfigs.USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConfigs.MESSAGES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConfigs.CONTACTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConfigs.RECENT_MESSAGES_TABLE);
        onCreate(db);
    }

    public User getUserDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        User user = null;
        Cursor cursor = db.rawQuery(DatabaseConfigs.GET_USER_DETAILS, null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                user = new User(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
            }
            cursor.close();
        }
        if (user != null) {
            Log.i(TAG, "getUserDetails: " + user.getUserName() + " :: " + user.getUserPhone());
        }
        return user;
    }

    public boolean saveUserDetails(FirebaseUser firebaseUser, User localUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(firebaseUser != null){
            contentValues.put(DatabaseConfigs.UID, firebaseUser.getUid());
            contentValues.put(DatabaseConfigs.USER_NAME, firebaseUser.getDisplayName());
            contentValues.put(DatabaseConfigs.USER_EMAIL, firebaseUser.getEmail());
            contentValues.put(DatabaseConfigs.USER_IMAGE, firebaseUser.getPhotoUrl().toString());
            contentValues.put(DatabaseConfigs.USER_PHONE, "");
        } else {
            contentValues.put(DatabaseConfigs.UID, localUser.getUid());
            contentValues.put(DatabaseConfigs.USER_NAME, localUser.getUserName());
            contentValues.put(DatabaseConfigs.USER_EMAIL, localUser.getUserEmail());
            contentValues.put(DatabaseConfigs.USER_IMAGE, localUser.getUserImage());
            contentValues.put(DatabaseConfigs.USER_PHONE, localUser.getUserPhone());
        }
        return db.insert(DatabaseConfigs.USER_TABLE, null, contentValues) != -1;
    }

    public boolean updateUserData(String id, String name, String phone, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConfigs.USER_NAME, name);
        contentValues.put(DatabaseConfigs.USER_PHONE, phone);
        contentValues.put(DatabaseConfigs.USER_IMAGE, image);
        db.update(DatabaseConfigs.USER_TABLE, contentValues, DatabaseConfigs.UID + " = ?", new String[]{id});
        return true;
    }

    public void saveMultipleContactsToDatabase(ArrayList<User> contactArrayList) {
        Log.i(TAG, "saveMultipleContactsToDatabase: Saving Multiple Contacts in Database => " + contactArrayList.size());
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery(DatabaseConfigs.DELETE_ALL_CONTACTS, null).close();
        for (User user : contactArrayList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConfigs.CONTACT_ID, user.getUid());
            contentValues.put(DatabaseConfigs.CONTACT_NAME, user.getUserName());
            contentValues.put(DatabaseConfigs.CONTACT_EMAIL, user.getUserEmail());
            contentValues.put(DatabaseConfigs.CONTACT_IMAGE, user.getUserImage());
            contentValues.put(DatabaseConfigs.CONTACT_PHONE, user.getUserPhone());
            db.insert(DatabaseConfigs.CONTACTS_TABLE, null, contentValues);
        }
    }

    public void saveContactToDatabase(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConfigs.CONTACT_ID, user.getUid());
        contentValues.put(DatabaseConfigs.CONTACT_NAME, user.getUserName());
        contentValues.put(DatabaseConfigs.CONTACT_EMAIL, user.getUserEmail());
        contentValues.put(DatabaseConfigs.CONTACT_IMAGE, user.getUserImage());
        contentValues.put(DatabaseConfigs.CONTACT_PHONE, user.getUserPhone());
        db.insert(DatabaseConfigs.CONTACTS_TABLE, null, contentValues);
    }

    public int getContactsCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + DatabaseConfigs.CONTACTS_TABLE +";";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                count = Integer.parseInt(cursor.getString(0));
            }
        }
        return count;
    }

    public TreeMap<String, String> getMultipleContactsFromDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        TreeMap<String, String> treeMap = new TreeMap<>();
        Cursor cursor = db.rawQuery(DatabaseConfigs.GET_CONTACTS, null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                if(treeMap.containsKey(name)){
                    name = Functions.encryptName(name, id);
                }
                treeMap.put(name, id);
            }
            cursor.close();
        }
        return treeMap;
    }

    public TreeMap<String, String> searchContacts(String searchString){
        SQLiteDatabase db = this.getWritableDatabase();
        TreeMap<String, String> treeMap = new TreeMap<>();
        String query = "SELECT * FROM " + DatabaseConfigs.CONTACTS_TABLE + " WHERE " + DatabaseConfigs.CONTACT_NAME + " LIKE " + searchString;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                if(treeMap.containsKey(name)){
                    name = Functions.encryptName(name, id);
                }
                treeMap.put(name, id);
            }
        }
        return treeMap;
    }

    public User getContactByID(String id, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        User user = null;
        Cursor cursor = db.rawQuery(DatabaseConfigs.GET_PARTICULAR_CONTACT, new String[]{id, phone});
        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                user = new User(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
            }
        }
        cursor.close();
        return user;
    }

    public boolean saveMessageToDatabase(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConfigs.MESSAGE_ID, message.getMessageID());
        contentValues.put(DatabaseConfigs.MESSAGE_TIME, message.getMessageTime());
        contentValues.put(DatabaseConfigs.MESSAGE_CONTENT, message.getMessageContent());
        contentValues.put(DatabaseConfigs.MESSAGE_FROM, message.getMessageFrom());
        contentValues.put(DatabaseConfigs.MESSAGE_TO, message.getMessageTo());
        return db.insert(DatabaseConfigs.MESSAGES_TABLE, null, contentValues) != -1;
    }

    public TreeMap<String, Message> getMessagesFromDatabase(String recipient){
        SQLiteDatabase db = this.getWritableDatabase();
        TreeMap<String, Message> messageTreeMap = new TreeMap<>();
        Cursor cursor = db.rawQuery(DatabaseConfigs.GET_RECIPIENT_MESSAGES, new String[]{recipient, recipient});
        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                String time = cursor.getString(1);
                Message message = new Message(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                messageTreeMap.put(time, message);
            }
        }
        cursor.close();
        return messageTreeMap;
    }

    public List<String> getRecentChats(String myPhone){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor recentChats = db.rawQuery(DatabaseConfigs.GET_RECENT_CHATS, null);
        if(recentChats.getCount() == 0){
            return new ArrayList<>();
        } else {
            List<String> contactsList = new ArrayList<>();
            while (recentChats.moveToNext()){
                String receiver = recentChats.getString(recentChats.getColumnIndex(DatabaseConfigs.MESSAGE_TO));
                String sender = recentChats.getString(recentChats.getColumnIndex(DatabaseConfigs.MESSAGE_FROM));

                if(!(contactsList.contains(receiver))){
                    contactsList.add(receiver);
                }

                if(!(contactsList.contains(sender))){
                    contactsList.add(sender);
                }
            }
            contactsList.remove(myPhone);
            return contactsList;
        }
    }
}
