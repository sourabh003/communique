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

## Using the app with different Firebase Database
Feel free to clone the code and use it as your liking. <br/>
To integrate this app with your firebase database just replace the `google-services.json` file in the app directory with your `google-services.json` file.

![Screenshot_1](https://user-images.githubusercontent.com/59859592/115279743-b7f4b000-a164-11eb-9ae3-61b785dcd168.png)

## Application Releases
You can try out the apk if your android version is above 8.0 <br/>Note: This is a debug version of the app so you might get a prompt by google while installing the app<br/>
APK link : https://drive.google.com/file/d/18qyTiavZ3UlkuC8TDF9R1hZ8EjhT6q84/view?usp=sharing
