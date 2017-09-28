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
# Chatroullete webrtc for Android

<!-- METADATA -->
<!-- Add links to JIRA, Google Drive, mailing list and other relevant resources -->
<!-- Add links to CI configs with build status and deployment environment, e.g.: -->
| environment | deployment            | status             |
|-------------|-----------------------|--------------------|
| Android     | [HockeyApp](https://rink.hockeyapp.net/manage/apps/598839) | [![Build Status](https://www.bitrise.io/app/efa82287989764d2/status.svg?token=wB5901VLKALuCDo5ZAMuyA&branch=master)](https://www.bitrise.io/app/efa82287989764d2) |

<!--- If applies, add link to app on Google Play -->

## Synopsis
<!-- Describe the project in few sentences -->
Chatroulette allows you to have a video chat with random stranger.

This projects showcase use of WebRTC on Android, signaling is done through Firebase.
Your firebase setup should provide at least one IceServer - for best results you should provide at least one Turn server to be able to make connection when peer to peer connection fails. 

[WebRTC for Android](https://webrtc.org/native-code/android/)
## Development
To run sample project you need to prepare firebase instance first.
### Setting up Firebase
1. Go to https://console.firebase.google.com/
2. Create new project
3. Add Firebase to your android app
    - To setup debug version insert package name `co.netguru.chatroulette.debug`
    - To setup release version insert package name `co.netguru.chatroulette`
4. Download google-services.json
5. Place it in App folder

Now we need to prepare Interactive Connectivity Establishment servers node. You will need your STUN/TURN servers credentials here.

6. Go to Database
7. Add new child name it `ice_servers`
8. Add new child to `ice_servers` named `0`
9. Add child to `0`
10. Insert ICE servers credentials
    - `uri : "turn:ipAddress"` or `uri : "stun:ipAddress"` in case of stun server
    
    optionally:
    
    - `username: "your username"`
    - `password: "your password"`
11. You can add more ICE servers to be used by WebRTC
12. This should be enough happy testing!

## STUN/TURN servers
### STUN (Session Traversal Utilities for NAT) 
STUN server will allow two parties to expose what their public IP addres is. In most cases we are hidden behind routers in local networks with private IP's. It's used only in first phase of connection while estabilishing peer to peer communication. There are many free STUN servers available as they are used only during connection initialization phase.

### TURN (Traversal Using Relay NAT)
TURN server is relay server which is used only when peer to peer connection cannot be estabilished, this can be caused for example by NAT restricions. When TURN server has to be used it might cause higher latency, as all the data has to go to the server and then to the other party, especially if the server is located far from both clients. TURN servers are obtainable mostly through paid services as lot of traffic is generated when TURN has to act as relay between clients, or you could host your own using [Coturn](https://github.com/coturn/coturn).

If you want to learn more about how STUN and TURN works go on and watch : https://youtu.be/p2HzZkd2A40?t=1123

### Public STUN/TURN servers
TBD

### Private STUN/TURN servers
#### External services
TBD
#### Set up your own STUN/TURN server
There are various options available out there we've tested Coturn and setup is fairly easy, you can follow this guide https://github.com/coturn/coturn/wiki/CoturnConfig .

### Sample integrations
<!-- Describe external service and hardware integrations, link to reference docs, use #### headings -->
- Firebase - WebRTC signaling solution based on firebase real time database - https://firebase.google.com/
- STUN/TURN servers

## Contribution
You're more than welcome to contribute or report an issue in case of any problems, questions or improvement proposals.

### Build types
<!-- List and describe build types -->
#### debug
 - debuggable
 - disabled ProGuard
 
#### release
 - uses full ProGuard configuration
 - enables zipAlign, shrinkResources
 - non-debuggable
 
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
      compile 'co.netguru.chatguru:chatguru:0.1.1@aar'
}
```

Copyright © 2017 [Netguru](http://netguru.co).

Licensed under the [GPLv3 License, BSD 3-Clause](LICENSE).
