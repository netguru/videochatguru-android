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
<!-- Add links to CI configs with build status and deployment environment, e.g.: 
| environment | deployment            | status             |
|-------------|-----------------------|--------------------|
| name        | HockeyApp/Fabric link | bitrise status tag |
-->
<!--- If applies, add link to app on Google Play -->

## Synopsis
<!-- Describe the project in few sentences -->
Chatroulette allows you to have a video chat with random stranger.

This projects showcase use of webrtc on Android, signaling is done through Firebase.
Your firebase setup should provide atleast one IceServer - for best result you should provide atleast one Turn server to be able to make connection when peer to peer fails.

[WebRTC for Android](https://webrtc.org/native-code/android/)
## Development
Create firebase instance and put google-services.json in to app.
### Architecture
<!-- Describe the main architectural pattern used in the project, optionally put a flowchart -->

### Integrations
<!-- Describe external service and hardware integrations, link to reference docs, use #### headings -->
- Firebase - WebRTC signaling solution based on firebase real time database -https://firebase.google.com/

### Coding guidelines
[Netguru Android code style guide](https://netguru.atlassian.net/wiki/display/ANDROID/Android+best+practices)
<!-- OPTIONAL: Describe any additional coding guidelines (if non-standard) -->

### Workflow & code review
[Netguru development workflow](https://netguru.atlassian.net/wiki/display/DT2015/Netguru+development+flow)
<!-- OPTIONAL: Describe workflow and code review process (if non-standard) --> 

## Testing
<!-- Describe the project's testing methodology -->
<!-- Examples: TDD? Using Espresso for views? What parts must be tested? etc -->

## Building
<!-- Aim to explain the process so that any new or external developer not familiar with the project can perform build and deploy -->

### Build types
<!-- List and describe build types -->
#### debug
 - debuggable
 - disabled ProGuard
 - uses built-in shrinking (no obfuscation)
 
#### release
 - uses full ProGuard configuration
 - enables zipAlign, shrinkResources
 - non-debuggable

### Product flavors
<!-- List and describe product flavors, purposes and dedicated deployment channels -->
#### develop
 - preview API, functional testing
 
#### production
 - production API, release

### Build properties
<!-- List all build properties that have to be supplied, including secrets. Describe the method of supplying them, both on local builds and CI -->

| Property         | External property name | Environment variable |
|------------------|------------------------|----------------------|
| HockeyApp App ID | HockeyAppId            | HOCKEY_APP_ID        |

#### Secrets
Follow [this guide](https://netguru.atlassian.net/wiki/pages/viewpage.action?pageId=33030753) 

#### Other properties

### ProGuard
<!-- Describe ProGuard configuration: is it enabled? Any unusual stuff? -->

## Deployment
<!-- Aim to explain the process so that any new or external developer can perform deploy -->

### Bitrise
<!-- Describe the Continuous Integration process: Bitrise workflows, global configs etc. -->

### HockeyApp / Fabric environments
<!-- Describe the deployment channels -->

### Supported devices
<!-- Describe the supported and target devices (do not put stuff that can be easily found in build.gradle files) --> 
