package com.example.communique.database;

public class DBQueries extends DBConstants{

    //User related Queries
    public static final String CREATE_USER_TABLE = "CREATE TABLE "+USER_TABLE+" ("+UID+" TEXT NOT NULL, "+USER_NAME+" TEXT, "+USER_EMAIL+" TEXT, "+USER_IMAGE+" TEXT, "+USER_PHONE+" TEXT)";
    public static final String GET_USER_DETAILS = "SELECT * FROM " + USER_TABLE;

    //Messages related Queries
    public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE " + MESSAGES_TABLE + " (" + MESSAGE_ID + " TEXT NOT NULL, " + MESSAGE_FROM + " TEXT, " + MESSAGE_TO + " TEXT, " + MESSAGE_TIME + " TEXT, " + MESSAGE_CONTENT + " TEXT)";
    public static final String GET_RECIPIENT_MESSAGES = "SELECT * FROM " + MESSAGES_TABLE + " WHERE " + MESSAGE_TO + " = ? OR " + MESSAGE_FROM + " = ?" ;
    public static final String GET_RECENT_CHATS = "SELECT " + MESSAGE_FROM + ", " + MESSAGE_TO + " FROM " + MESSAGES_TABLE;
    public static final String DELETE_MESSAGES_FROM_LOCAL_DATABASE = "DELETE FROM " + MESSAGES_TABLE + " WHERE " + MESSAGE_TO + " = ? OR " + MESSAGE_FROM + " = ?" ;
    public static final String CREATE_RECENT_MESSAGES_TABLE = "CREATE TABLE " + RECENT_MESSAGES_TABLE + " (" + MESSAGE_FROM + " TEXT NOT NULL, " + MESSAGES_COUNT + " TEXT)";

    //Contacts related Queries
    public static final String CREATE_CONTACTS_TABLE = "CREATE TABLE "+CONTACTS_TABLE+" ("+CONTACT_ID+" TEXT NOT NULL, "+CONTACT_NAME+" TEXT, "+CONTACT_EMAIL+" TEXT, "+CONTACT_IMAGE+" TEXT, "+CONTACT_PHONE+" TEXT)";
    public static final String GET_CONTACTS = "SELECT * FROM " + CONTACTS_TABLE;
    public static final String DELETE_ALL_CONTACTS = "DELETE FROM " + CONTACTS_TABLE;
    public static final String GET_PARTICULAR_CONTACT = "SELECT * FROM " + CONTACTS_TABLE + " WHERE " + CONTACT_ID + " = ? OR " + CONTACT_PHONE + " = ?";

}
