package com.example.communique.database;

public class DatabaseConfigs {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "communique.db";

    public static final String USER_TABLE = "currentUserDetails";
    public static final String MESSAGES_TABLE = "messagesTable";
    public static final String RECENT_MESSAGES_TABLE = "recentMessagesTable";
    public static final String CONTACTS_TABLE = "contactsTable";

    public static final String UID = "uid";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_PHONE = "user_phone";

    public static final String MESSAGE_ID = "mid";
    public static final String MESSAGE_TO = "mto";
    public static final String MESSAGE_FROM = "mfrom";
    public static final String MESSAGE_TIME = "mtime";
    public static final String MESSAGE_CONTENT = "mcontent";
    public static final String MESSAGES_COUNT = "mcount";

    public static final String CONTACT_ID = "cid";
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_EMAIL = "contact_email";
    public static final String CONTACT_IMAGE = "contact_image";
    public static final String CONTACT_PHONE = "contact_phone";

    //User related Queries
    public static final String CREATE_USER_TABLE = "CREATE TABLE "+USER_TABLE+" ("+UID+" TEXT NOT NULL, "+USER_NAME+" TEXT, "+USER_EMAIL+" TEXT, "+USER_IMAGE+" TEXT, "+USER_PHONE+" TEXT)";
    public static final String GET_USER_DETAILS = "SELECT * FROM " + USER_TABLE;

    //Messages related Queries
    public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE " + MESSAGES_TABLE + " (" + MESSAGE_ID + " TEXT NOT NULL, " + MESSAGE_TIME + " TEXT, " + MESSAGE_CONTENT + " TEXT, " + MESSAGE_FROM + " TEXT, " + MESSAGE_TO + " TEXT)";
    public static final String GET_RECIPIENT_MESSAGES = "SELECT * FROM " + MESSAGES_TABLE + " WHERE " + MESSAGE_TO + " = ? OR " + MESSAGE_FROM + " = ?" ;
    public static final String GET_RECENT_CHATS = "SELECT " + MESSAGE_FROM + ", " + MESSAGE_TO + " FROM " + MESSAGES_TABLE;
    public static final String DELETE_MESSAGES_FROM_LOCAL_DATABASE = "DELETE FROM " + MESSAGES_TABLE + " WHERE " + MESSAGE_TO + " = ? OR " + MESSAGE_FROM + " = ?" ;
    public static final String CREATE_RECENT_MESSAGES_TABLE = "CREATE TABLE " + RECENT_MESSAGES_TABLE + " (" + MESSAGE_FROM + " TEXT NOT NULL, " + MESSAGES_COUNT + " TEXT)";

    //Contacts related Queries
    public static final String CREATE_CONTACTS_TABLE = "CREATE TABLE "+CONTACTS_TABLE+" ("+CONTACT_ID+" TEXT NOT NULL, "+CONTACT_NAME+" TEXT, "+CONTACT_EMAIL+" TEXT, "+CONTACT_IMAGE+" TEXT, "+CONTACT_PHONE+" TEXT UNIQUE)";
    public static final String GET_CONTACTS = "SELECT * FROM " + CONTACTS_TABLE;
    public static final String DELETE_ALL_CONTACTS = "DELETE FROM " + CONTACTS_TABLE;
    public static final String GET_PARTICULAR_CONTACT = "SELECT * FROM " + CONTACTS_TABLE + " WHERE " + CONTACT_ID + " = ? OR " + CONTACT_PHONE + " = ?";
}
