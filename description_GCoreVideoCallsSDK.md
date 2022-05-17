# System requirements

* Anroid min: 4.3 Jelly Bean (API level 18).

# Integration Video calls SDK

## Importing the Video calls SDK and configuring the project

1. Create a new project
2. Copy `GCoreVideoCallsSDK` and `mediasoup-android-client` to the project folder
3. Add them to your build.gradle file:
   ```gradle
   implementation project(':GCoreVideoCallsSDK')
   implementation project(':mediasoup-android-client')
   ```	
4. Because at the moment the SDK is delivered as a .aar archive, you also need to specify the dependencies that are necessary for the SDK to work in the build.gradle file:
    ```gradle
    implementation "androidx.lifecycle:lifecycle-extensions:$LIFECYCLE_VERSION"

    implementation "com.google.dagger:dagger:$DAGGER_VERSION"
    kapt "com.google.dagger:dagger-compiler:$DAGGER_VERSION" 

    implementation "com.jakewharton.timber:timber:$TIMBER_VERSION"  

    implementation("io.reactivex.rxjava2:rxjava:$RX_JAVA_VERSION")
    implementation("io.reactivex.rxjava2:rxandroid:$RX_VERSION")  

    implementation "org.protoojs.droid:protoo-client:$PROTO_VERSION"   

    implementation("com.squareup.okhttp3:okhttp:$OKHTTP_VERSION")
    implementation("com.squareup.okhttp3:logging-interceptor:$OKHTTP_VERSION")
    ```
    You can see the versions of the libraries in the versions.gradle    file.

## Initializing the Video calls SDK

1. Initializing the SDK

   ```kotlin
    GCoreMeet.instance.init(
       applicationContext: Application, 
       logger: gcore.videocalls.meet.logger.Logger?, 
       enableLogs: Boolean
    ) 
   ```

2. Defining the parameters for connecting to the server

   ```kotlin   
   GCoreMeet.instance.clientHostName = "meet.gcorelabs.com"
   GCoreMeet.instance.roomManager.displayName = "John Snow"
   GCoreMeet.instance.setRoomId("serv01234")
   GCoreMeet.instance.roomManager.peerId = "user09876"
   GCoreMeet.instance.roomManager.isModer = true
   ```

   | Method | Type | Description | 
   |--|--|--| 
   | setRoomId | String | Room ID to connect to<br>*Example:*   `roomId:     "serv01234"` |   

   | Parameter | Type | Description| 
   |--|--|--| 
   |displayName | String | Set display name of participant<br>[Link for   extra  details in knowledge base](https://gcorelabs.com/support/articles/4404682043665/#h_01FBPQAEZZ1GR7SF7G7TBAYJWZ)   <br>*Example:* `displayName:   "John Snow"`| 
   | peerId | String? (optional) | ID of a participant from your   internal  system. Please specify userID if you have your own. Or just   leave this   field blank, then the value will be generated   automatically.<br>[Link for extra details in knowledge base](https://gcorelabs.com/support/articles/4404682043665#h_01FBPQC18B1E3K58C05A8E81Y7)<br>*Example:* `peerId:"user0000000001"`| 
   | clientHostName | String? (optional) | In this parameter, the client passes the domain name that he uses to operate the web version of the mit. Value: domain only, no protocol specified   <br>*Example:*    `clientHostName: "meet.gcorelabs.com"`| 
   | isModer | Boolean | if `true`, then the user will be a moderator, and additional functionality is available for him

3. The following permissions are required for the SDK to work
   ```xml
   <uses-permission android:name="android.permission.INTERNET"/>
   <uses-permission android:name="android.permission.CALL_PHONE"/>
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
   ```
   
4. Before joining a room, you need to connect
   ```kotlin    
   GCoreMeet.instance.startConnection(context)
   ```

5. Before connecting to the room, we can set the settings for audio and video
   ```kotlin
   roomManager.options.startWithCam = true
   roomManager.options.startWithMic = true
   ```

6. Joining a room
   ```kotlin
   roomManager = GCoreMeet.instance.roomManager
   if (roomManager.isClosed()) {
       roomManager.join()
   }
   ```

## Subscribe to changes occurring in the call room
   ```kotlin   
   /// Connection state change 
   roomManager.roomProvider.connected
   
   /// List of all peers in the room
   roomManager.roomProvider.peers 
   /// or
   GCoreMeet.instance.getPeers()
   
   /// Room state
   roomManager.roomProvider.roomInfo.observeForever{ roomInfo->
      roomInfo.activeSpeakerIds   // Participants who are currently talking
      roomInfo.connectionState    // Room state            
   }
   
   /// Local state
   roomManager.roomProvider.me.observeForever { localState: LocalState ->
        
   }

    /// User
 
    /// Room invitation request state if the waiting room is enabled
    roomManager.roomProvider.waitingState.observeForever{state: WaitingState -> }
    /// may be
    /// NOTHING 
    /// IN_WAITING - waiting for moderator response
    /// ACCEPTED - moderator allowed entry
    /// REJECTED - moderator denied entry
    
    /// The moderator removed the user from the room
    roomManager.roomProvider.closedByModerator.observeForever{}
    
    /// Permission to use the microphone/camera
    roomManager.roomProvider.micAllowed.observeForever{allowet: Boolean -> }
    roomManager.roomProvider.camAllowed.observeForever{allowet: Boolean -> }
    
    /// The moderator himself requested the inclusion of a microphone/camera
    roomManager.roomProvider.acceptedAudioPermission.observeForever{}
    roomManager.roomProvider.acceptedVideoPermission.observeForever{}
    
    /// The moderator approved the user's pull request 
    roomManager.roomProvider.acceptedAudioPermissionFromModerator.observeForever{}
    roomManager.roomProvider.acceptedVideoPermissionFromModerator.observeForever{}

    /// Occurs if the user has a microphone/camera blocked, but he wants to turn them on
    roomManager.askUserConfirmMic.observeForever{}
    roomManager.askUserConfirmCam.observeForever{}
   
    /// Moderator
     
    roomManager.roomProvider.requestToModerator.observeForever { data: RequestPeerData ->
        /// RequestPeerData contains userName, peerId, requestType 
        /// requestType can be AUDIO, VIDEO, SHARE requests for microphone, camera, demonstration,  respectively
    }
   
   ```

## Display room members

To display video from the device's camera, use LocalVideoView
```xml
 <gcore.videocalls.meet.ui.view.me.LocalVideoView
     android:id="@+id/localVideo"
     android:layout_width="wrap_content"
     android:layout_height="wrap_content"/>
```

```kotlin
 localVideo.connect()
``` 

To display the peers of other participants, we use PeerVideoView
```xml
 <gcore.videocalls.meet.ui.view.peer.PeerVideoView
     android:id="@+id/peerVideo"
     android:layout_width="match_parent"
     android:layout_height="match_parent"/>
```     
```kotlin
peer_video_view.connect(peer.id)
```

## Changing the state of the camera and microphone (these methods are also available in LocalVideoView)

   ```kotlin
   /// Enabling the camera/microphone
   GCoreMeet.instance.roomManager.enableCam() 
   GCoreMeet.instance.roomManager.enableMic()

   /// Disabling the camera/microphone 
   GCoreMeet.instance.roomManager.disableCam()
   GCoreMeet.instance.roomManager.disableMic()

   /// Mute/unmute the microphone
   GCoreMeet.instance.roomManager.muteMic()
   GCoreMeet.instance.roomManager.unmuteMic()

   /// Switching the state of the camera/microphone
   GCoreMeet.instance.roomManager.disableEnableCam()
   GCoreMeet.instance.roomManager.disableEnableMic()
   
   /// Camera switching 
   roomManager.changeCam()
   ```

##  User functionality 
   ```kotlin
   /// Request the moderator to turn on the microphone, used if the moderator has forbidden it to the user 
   GCoreMeet.instance.roomManager.askModeratorEnableMic()

   /// Same with camera
   GCoreMeet.instance.roomManager.askModeratorEnableCam()
   ```

##  Moderator functionality
    
The moderator can interact with the user through the `roomManager` methods, which are passedthe user's `peerId` parameter:
* `enableAudioByModerator/disableAudioByModerator` - to request to enable/disable the user'smicrophone 
* `enableVideoByModerator/disableVideoByModerator` - to request to enable/disable the user'scamera
* `enableShareByModerator/disableShareByModerator` - to request to enable/disable the user'ssharing

are used to respond, after the `requestToModerator` event, if the moderator has received arequest to turn on the microphone, camera or sharing.
