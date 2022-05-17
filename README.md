# G-Core Labs Demo - Video Calls on Android

# Introduction
Setup real-time communication between users in 15 minutes in your Android project instead of 7 days of work and setting parameters of codecs, network, etc. This demo project is a quick tutorial how to video call from your own mobile app to many users.

# Features

1. Real time communication with WebRTC   
2. Playback video from remote users  
3. Flip camera, connection, enable/disable camera, mute/unmute microphone

# Quick start

1. Run the project in Android Studio where you can test it on a real device or with an Android emulator.
2. Specify roomId and hostName to create/join a room
3. Enter a name, turn on video/audio on the preview screen
4. Click join button and enjoy real-time communication!

# Setup of project

1. SDK - [GCoreVideoCallsSDK](https://github.com/G-Core/android-demo-video-calls/blob/releases/release_1.0.0/description_GCoreVideoCallsSDK.md#:~:text=Blame-,System%20requirements,-Anroid%20min%3A%204.3)  
To work with G-Core Labs services (video calls), we need GCoreVideoCallsSDK. This SDK allows you to establish a connection to the server and receive events from it, as well as send and receive data streams from other users.

2. Permissions  
The following permissions are required for the SDK to work:
   ```xml
   <uses-permission android:name="android.permission.INTERNET"/>
   <uses-permission android:name="android.permission.CALL_PHONE"/>
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
   ```
    They need to be specified in the AndroidManifest.xml file of your project.

3. API  
The SDK receives events from the server and interacts with it using the GCoreMeet.instance object, you can see the full set of features [here](https://github.com/G-Core/android-demo-video-calls/blob/releases/release_1.0.0/description_GCoreVideoCallsSDK.md#:~:text=Subscribe%20to%20changes%20occurring%20in%20the%20call%20room).

# Requirements

1. Anroid min: 4.3 Jelly Bean (API level 18) - for SDK.   
   Android min: 7.0 Nougat (API level 24) - for demo project.
2. Real device or Android emulator
3. The presence of an Internet connection on the device.
4. The presence of a camera and microphone on the device.

# Screenshots
<img src="https://user-images.githubusercontent.com/100352152/168834191-2c02fe09-caf7-4c85-8654-461c1b0b8d5c.png" width="300"> <img 
    src="https://user-images.githubusercontent.com/100352152/168834213-4f2efdfb-fbc7-4775-ab0a-b11922a88d5a.png" width="300">
<img src="https://user-images.githubusercontent.com/100352152/168834242-5dad7b3c-9905-437a-8186-134cffcff56e.png" width="300">
<img src="https://user-images.githubusercontent.com/100352152/168834266-8be80567-a528-4158-abd9-067aa4abe910.png" width="300">
<img src="https://user-images.githubusercontent.com/100352152/168834291-7fb29d11-8d4e-4758-8d53-cc36e63de968.png" width="300">

# License
    Copyright 2022 G-Core Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
