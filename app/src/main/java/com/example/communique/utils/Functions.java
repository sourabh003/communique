package com.example.communique.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.communique.database.Database;
import com.example.communique.helpers.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.TreeMap;

public class Functions {

    static String TAG = "Functions";

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
        int count;
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static ArrayList<String> fetchContactsFromPhone(Context context) {
        String contactId, contactName, contactNumber;
        ArrayList<User> contactsDetailsList = new ArrayList<>();
        ArrayList<String> contactsNameList = new ArrayList<>();
        Database dbHelper = new Database(context);
        User myDetails = dbHelper.getUserDetails();
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
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
                        if (contactNumber.length() > 10) {
                            contactNumber = contactNumber.substring(contactNumber.length() - 10, contactNumber.length());
                        }
                        if (!(contactNumber.contains("+91"))) {
                            contactNumber = "+91" + contactNumber;
                        }
                        contactName = encryptName(contactName, contactId);
                        User newContact = new User(
                                contactId,
                                contactName,
                                "",
                                "",
                                contactNumber
                        );
                        if (!(contactNumber.equals(myDetails.getUserPhone()))) {
                            contactsDetailsList.add(newContact);
                            contactsNameList.add(contactName);
                        }
                    }
                    phoneCursor.close();
                }
            }
        }
        cursor.close();
        new Thread(() -> {
            dbHelper.saveMultipleContactsToDatabase(contactsDetailsList);
            Functions.deleteFileFromInternalStorage(Constants.CONTACTS_CACHE_FILE, context);
            try {
                Functions.saveToInternalStorage(contactsNameList.toString(), Constants.CONTACTS_CACHE_FILE, context);
            } catch (IOException e) {
                Log.e(TAG, "fetchContactsFromPhone: Error saving cache file => " + e);
                e.printStackTrace();
            }
        }).start();
        Collections.sort(contactsNameList);
        return contactsNameList;
    }

    public static String encryptName(String name, String id) {
        return name + Constants.KEY_VALUE_SEPERATOR + id;
    }

    public static String decryptName(String name) {
        return name.split(Constants.KEY_VALUE_SEPERATOR)[0];
    }

    public static void openKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                activity.getCurrentFocus().getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean isKeyboardOpen(Activity activity){
        return ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).isAcceptingText();
    }

    public static void closeKeyboard(Activity activity, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static boolean saveToInternalStorage(String data, String filename, Context context) throws IOException {
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        //default mode is PRIVATE, can be APPEND etc.
        fos.write(data.getBytes());
        fos.close();
        return true;
    }

    public static String readFileFromInternalStorage(String filename, Context context) throws IOException {
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

    public static boolean checkFileInInternalStorage(String filename, Context context) {
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        return file.exists();
    }

    public static boolean deleteFileFromInternalStorage(String filename, Context context) {
        if (checkFileInInternalStorage(filename, context)) {
            File dir = context.getFilesDir();
            File file = new File(dir, filename);
            return file.delete();
        }
        return false;
    }

    public static TreeMap<String, String> stringToMap(Context context, String mapData) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        mapData = mapData.substring(1, mapData.length() - 1);
        String[] array = mapData.split(",");
        for(String item : array){
            treeMap.put(item.split("=")[0].trim(), item.split("=")[1].trim());
        }
        return treeMap;
    }

    public static ArrayList<String> stringToArray(String contactsString) {
        ArrayList<String> tempArray = new ArrayList<>();
        contactsString = contactsString.substring(1, contactsString.length() - 1);
        String[] array = contactsString.split(",");
        for(String item : array){
            tempArray.add(item.trim());
        }
        return tempArray;
    }
}