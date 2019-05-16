# Naturae - Nature Themed Social Application

Senior Group Project for California State University Long Beach
Class: CECS 491 - Spring 2019
An Android Java Mobile Application

A mobile crowd-sourcing application for nature lovers, observers, and researchers. Users can register with the app
and upload their own pictures of nature. The app will record the location where the image was taken and pin it on
the map for other viewers to see. Viewers can network with each other by the friendslist and chat system.

## Deployment
The application was developed and tested with Android Studio 3.0
It can be launched using the AVD emulator built into the IDE. For best performance, connect 
an Android device to your system and it can be used instead of the virtual emulator.
Sync the gradle file and build the application.  

You will require access to a running Naturae Server. View the repository here 
[Naturae Server](https://github.com/HappyLyfe123)

## Built With
* [Android Studio](https://developer.android.com/studio)
* [Google Maps API](https://developers.google.com/maps/documentation/) Used to display the live map and create pins
* [Google Protobuffers](https://github.com/protocolbuffers/protobuf)
* [gRPC Java](https://github.com/grpc/grpc-java)
* [Scaledrone](https://www.scaledrone.com/)

## Features
* Login and Registration
* Email Verification
* Image Posting and Viewing
* Map Search
* Friendslist 
* 1 on 1 live chat
* Profile and Avatar
* Password Reset

## Limitations
This project has been designed to run on Android Devices through the use of
Java in Android Studio. Certain features of the application will not work without initial setup because they rely on certain APIs.
The maps portion requires that you have a registered Google Maps API key set in the project manifest. Setting the key will 
enable the map. In addition, the chat requires a valid Scaledrone key. 

## Contributors

* **Visal Hok** -  [HappyLyfe123](https://github.com/HappyLyfe123)
* **Steven Lim** - [LimStevenLBW](https://github.com/LimStevenLBW)
* **Brian Ashley** -  [xbbasherx](https://github.com/xbbasherx)
* **Nanae Aubry** - [nanaeaubry](https://github.com/nanaeaubry)
* **Marcus Herndon** - [marcus6897](https://github.com/marcus6897)

## License

This project is licensed under the MIT License, see the [LICENSE.md](LICENSE.md) file for details
