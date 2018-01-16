<!-- 
    Couple of points about editing:
    
    1. Keep it SIMPLE.
    2. Refer to reference docs and other external sources when possible.
    3. Remember that the file must be useful for new / external developers, and stand as a documentation basis on its own.
    4. Try to make it as informative as possible.
    5. Do not put data that can be easily found in code.
    6. Include this file on ALL branches.
-->

<!-- Put your project's name -->
# VideoChatGuru simple webrtc for Android

<!-- METADATA -->
<!-- Add links to JIRA, Google Drive, mailing list and other relevant resources -->
<!-- Add links to CI configs with build status and deployment environment, e.g.: -->
| environment | status             |
|-------------|--------------------|
| Android     | [![Build Status](https://www.bitrise.io/app/efa82287989764d2/status.svg?token=wB5901VLKALuCDo5ZAMuyA&branch=master)](https://www.bitrise.io/app/efa82287989764d2) |

<!--- If applies, add link to app on Google Play -->
## About
VideoChatGuru is an open-source solution written entirely in Kotlin, based on a [WebRTC](https://webrtc.org/native-code/android/) implementation. It’s not bound with any service, and you are free to choose whether you are going to use an established service provider or host your own. VideoChatGuru prioritises peer-to-peer connections, thanks to which we reduce delays to a minimum.

VideoChatGuru wraps the [WebRTC](https://webrtc.org/native-code/android/) API, providing a friendlier access to its features that you are going to need for implementing video chats. We also provide our Chat&Roll example implementation, so that you can see VideoChatGuru in action. Thanks to the fact that we used Firebase for signalling, it should be much easier to learn and understand how webRTC signalling works by observing it unfold live.

## VideoChatGuru setup
- Create layout for your Video Chat - you will need to provide two SurfaceViewRenderer views for remote and local video.
- Create instance of `WebRtcClient` all params are optional and default ones should suffice most of use cases.
- Attach views using `webRtcClient.attachRemoteView(view)` and `webRtcClient.attachLocalView(view)` respectiviely there are also methods which allows you to unbind those.
- Initialize peer connection by calling `webRtcClient.initializePeerConnection(...)` method. Here you will need to provide list of Interactive Connectivity Establishment servers(read more below) and implementation for three listeners that are required to make everything work.
    
    - `PeerConnectionListener.onIceCandidate` and `PeerConnectionListener.onIceCandidatesRemoved` - those callbacks are called when WebRTC produces or removes ICE candidates, you are responsible to pass those to the other party through any other estabilished communication channel. Other party should handle those respectively by calling `addRemoteIceCandidate` or `removeRemoteIceCandidate`
    - `WebRtcOfferingActionListener` - callbacks launched for offering party - one which will initialize call using create offer. `LocalSessionDescription` object which is passed in `onOfferRemoteDescription` should be passed to the answering party and handled using `handleRemoteOffer` method.
    - `WebRtcAnsweringPartyListener` - callbacks launched for answering party - one which will call handleRemoteOffer method. `LocalSessionDescription` object which is passed in `onSuccess` should be passed to the offering party and handled using `handleRemoteAnswer` method.
 - At this point `WebRtcClient` setup is done and you should initialize handshake process on one of the clients using `createOffer` method handling all the callbacks as described earlier.
    
 You can also reffer to the sample.
 
 ## What this library won't do:
WebRTC is signaling agnostic meaning that it's your responsibility to provide communication channell that will allow to go through handshake phase. You are free to use your own solutions based on for example: FCM, WebSockets, Pooling, Firebase and any other that allows you to exchange messages beetwen clients in real time.

You can reffer to our sample for solution based on Firebase that allowed us to create and implement Chat Roullette logic. Firebase will also allow you to easily observe handshake process in real time.

## Sample
Chat&Roll sample allows you to have a video chat with random stranger, project showcase use of VideoChatGuru WebRTC wrapper on Android. Signaling is done through Firebase. Your Firebase setup should provide at least one IceServer - for best results you should provide at least one Turn server to be able to make connection when peer to peer connection fails. 

You can download Chat&Roll sample app from Google Play: https://play.google.com/store/apps/details?id=co.netguru.android.chatandroll

## Development
To run sample project you need to prepare Firebase instance first.
### Setting up Firebase
1. Go to https://console.firebase.google.com/
2. Create new project
3. Add Firebase to your android app
    - To setup debug version insert package name `co.netguru.android.chatandroll.debug`
    - To setup release version insert package name `co.netguru.android.chatandroll`
4. Download google-services.json
5. Place it in App folder

#### Setting up Interactive Connectivity Establishment servers node.
You will need your STUN/TURN servers credentials here.

1. Go to Database
2. Add new child name it `ice_servers`
3. Add new child to `ice_servers` named `0`
4. Add child to `0`
5. Insert ICE servers credentials
    - `uri : "turn:ipAddress"` or `uri : "stun:ipAddress"` in case of stun server
    
    optionally:
    
    - `username: "your username"`
    - `password: "your password"`
 6. You can add more ICE servers to be used by WebRTC
 7. This should be enough happy testing!

## STUN/TURN servers
### STUN (Session Traversal Utilities for NAT) 
STUN server will allow two parties to expose what their public IP addres is. In most cases we are hidden behind routers in local networks with private IP's. It's used only in first phase of connection while estabilishing peer to peer communication. There are many free STUN servers available as they are used only during connection initialization phase.

### TURN (Traversal Using Relay NAT)
TURN server is relay server which is used only when peer to peer connection cannot be estabilished, this can be caused for example by NAT restricions. When TURN server has to be used it might cause higher latency, as all the data has to go to the server and then to the other party, especially if the server is located far from both clients. TURN servers are obtainable mostly through paid services as lot of traffic is generated when TURN has to act as relay between clients, or you could host your own using [Coturn](https://github.com/coturn/coturn).

If you want to learn more about how STUN and TURN works go on and watch : https://youtu.be/p2HzZkd2A40?t=1123

### Public STUN/TURN servers
You can find many public STUN servers on the web, you might also want to try out:
- http://numb.viagenie.ca/
Numb provides both STUN and TURN servers free of charge.
Take in mind that using public servers might not be the best choice for production code.
### Private STUN/TURN servers
#### External services
Many private services provides their STUN servers free of charge. You can also find free TURN servers with limited amount of bandwidth to use up.
#### Set up your own STUN/TURN server
There are various options available out there we've tested Coturn and setup is fairly easy, you can follow this guide https://github.com/coturn/coturn/wiki/CoturnConfig .

### Sample integrations
<!-- Describe external service and hardware integrations, link to reference docs, use #### headings -->
- Firebase - WebRTC signaling solution based on firebase real time database - https://firebase.google.com/
- STUN/TURN servers

## Contribution
You're more than welcome to contribute or report an issue in case of any problems, questions or improvement proposals.

### How to contribute
* Fork it ( https://github.com/netguru/videochatguru-android/fork )
* Create your feature branch (git checkout -b my-new-feature)
* Commit your changes (git commit -am 'Add some feature')
* Push to the branch (git push origin my-new-feature)
* Create a new Pull Request
 
## Download
To use this library in your project, place the following code in your top hierarchy build.gradle file:
```groovy
allprojects {
    repositories {
        maven {  url 'https://dl.bintray.com/netguru/maven/' }
    }
}
```

Just add the following dependency in your app's build.gradle:
```groovy
dependencies {
      compile 'co.netguru.videochatguru:videochatguru:0.1.2@aar'
}
```

Copyright © 2017 [Netguru](http://netguru.co).
