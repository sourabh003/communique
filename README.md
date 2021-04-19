# Communique

## Introduction
Communique is a Realtime Chatting Android Application which can send messages in realtime. 

## Technology Stack
Communique is built using the following stack
* Java - Main language
* SQLite - Local Database for saving message on device
* Firebase - Realtime Database for transferring messages in realtime 

## Workflow
Communique runs on an Android Device. When sending a message, the messages are sent to the Firebase Database then the target device fetches the message and save them to Sqlite database locally. 

![communique workflow](https://user-images.githubusercontent.com/59859592/115279108-e756ed00-a163-11eb-8718-19edbebc8eb2.jpg)
