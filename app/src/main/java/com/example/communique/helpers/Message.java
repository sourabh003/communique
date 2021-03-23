package com.example.communique.helpers;

public class Message {
    public String messageID, messageTime, messageContent, messageFrom, messageTo;

    public Message(String messageID, String messageTime, String messageContent, String messageFrom, String messageTo){
        this.messageID = messageID;
        this.messageTime = messageTime;
        this.messageContent = messageContent;
        this.messageFrom = messageFrom;
        this.messageTo = messageTo;
    }

    public Message(){

    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }
}
