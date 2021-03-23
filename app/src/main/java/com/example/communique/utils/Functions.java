package com.example.communique.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.communique.database.DBHelper;
import com.example.communique.helpers.Contact;
import com.example.communique.helpers.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Functions {
    public static void showLoading(ProgressBar loading, boolean condition) {
        if (condition) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.INVISIBLE);
        }
    }

    public static String getUniqueID() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    public static int getContactsCount(Context context) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        return cursor.getCount();
    }

    public static List<Contact> getContactsFomPhone(Context context) throws JSONException {
        String contactId, contactName = null, contactNumber = null;
        List<Contact> contactList = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context);
        User myDetails = dbHelper.getCurrentUserDetails();
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                JSONObject contact = new JSONObject();
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    Cursor phoneCursor = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);

                    if (phoneCursor.moveToNext()) {
                        contactNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactNumber = contactNumber.replaceAll("\\(", "");
                        contactNumber = contactNumber.replaceAll("\\)", "");
                        contactNumber = contactNumber.replaceAll("-", "");
                        contactNumber = contactNumber.replaceAll(" ", "");
                        if(!(contactNumber.contains("+91"))){
                            contactNumber = "+91" + contactNumber;
                        }
                        Contact newContact = new Contact(
                                contactId,
                                contactName,
                                "",
                                "",
                                contactNumber
                        );
                        if(!(contactNumber.equals(myDetails.getUserPhone()))){
                            contactList.add(newContact);
                        }
                    }
                    phoneCursor.close();
                }
            }
            dbHelper.addContactsToDatabase(contactList);
//            if(Functions.checkFile(Constants.ALL_CONTACTS_FILE, context)){
//                Functions.deleteFile(Constants.ALL_CONTACTS_FILE, context);
//            }
//            Functions.writeFile(contacts.toString(), Constants.ALL_CONTACTS_FILE, context);
        }
        cursor.close();
        return contactList;
    }

    public static boolean writeFile(String data, String filename, Context context) throws IOException {
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        //default mode is PRIVATE, can be APPEND etc.
        fos.write(data.getBytes());
        fos.close();
        return true;
    }

    public static String readFile(String filename, Context context) throws IOException {
        FileInputStream fis = context.openFileInput(filename);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        return sb.toString();
    }

    public static boolean checkFile(String filename, Context context){
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        return file.exists();
    }

    public static boolean deleteFile(String filename, Context context){
        if(checkFile(filename, context)){
            File dir = context.getFilesDir();
            File file = new File(dir, filename);
            return file.delete();
        }
        return false;
    }

    public static ArrayList<String> stringToArray(){
        String arr = "[1,2]";
        String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
        return new ArrayList<>(Arrays.asList(items));
    }

    public static ArrayList<String> stringToArray(String input) {
        ArrayList<String> arrayList = new ArrayList<>();
        String[] items = input.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
        arrayList.addAll(Arrays.asList(items));
        return arrayList;
    }
}