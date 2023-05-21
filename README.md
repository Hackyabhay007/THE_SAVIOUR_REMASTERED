

# The Saviour - Emergency Toolkit
[The Saviour - Emergency Toolkit on Play Store](https://play.google.com/store/apps/details?id=com.hackydesk.thesaviour)

The Saviour - Emergency Toolkit is a comprehensive mobile application that provides various emergency features to ensure user safety and security. It offers both online and offline SOS modes, a share journey feature, emergency dialer, rapid SOS activation, manage guardians, share address functionality, and a "Make India Safe" feature where users can contribute by submitting reviews about their area. The app aims to make the country safer by providing essential emergency tools and enabling users to share their experiences.

## Tech Stack

- Programming Languages: Java, XML
- SDK:  Android SDK, Google Maps SDK
- Api : Google maps Api
- Backend: Firebase (push notifications, Firestore,Realtime Database)

## Features

### SOS Mode
- Provides online and offline SOS modes.
- Checks device internet connection to determine the mode.
- Online SOS mode includes:
  - Battery status
  - Live location
  - Ring device mode (high alarm ring)
  - Directions to the user
  - Total distance to the device
  - Speed of the device
  - Current battery status
  - Data fetch time
  - Direct call to the user
- Offline SOS mode includes:
  - Battery status
  - Last known location
  - Ring device mode (high alarm ring)
  - Directions to the user
  - Total distance to the device
  - Last known battery status
  - Data fetch time
  - Direct call to the user

### Share Journey Feature
- Allows users to share their live location and device status.
- Contains information such as device speed, device battery, device distance from guardian, data fetch time, date, and directions to the user.

### Emergency Dialer
- Provides a list of all India emergency numbers.
- Users can tap on a number to dial it directly.

### Rapid SOS Mode
- Enables users to initiate SOS without opening the app.
- Works even if the device is locked and internet is on or off.
- Activation is done by clicking the volume up button seven times within four seconds.
- Accessibility permission is used to monitor volume up press events for rapid SOS activation. The app does not collect or send any information.

### Manage Guardians
- Allows users to manage all guardians in one place.

### Share Address
- Enables users to share or copy their current location address.

### Make India Safe
- Encourages users to contribute by submitting reviews about their area.
- Area scores are displayed based on user reviews.

### Rapid SOS
- Provides functionality to activate SOS mode without opening the app.
- Activation is done by clicking the volume up button seven times within four seconds.
- Works even if the device is locked.
- Accessibility permission is used for monitoring volume up press events. No information is collected or sent.

### SOS and Share Journey Tracking
- Dedicated Guardian Mode allows tracking of SOS and share journey (last location, device status).
- Two modes available: online and offline.
- Online mode is shown when SOS is initiated by the user with an internet connection.
- Offline mode is shown when SOS is initiated without an internet connection.
- Guardians receive a high alarm sound when SOS is activated by the user.

### Make Country Safe
- Provides features for users to review their area.
- Users can rate different categories to get their area score.

## Note
The app prioritizes user privacy and security. It uses accessibility permission solely for monitoring volume up press events to activate the rapid SOS feature. The app does not collect any personal information or send any information without user consent.

Feel free to explore the various features of The Saviour - Emergency Toolkit
