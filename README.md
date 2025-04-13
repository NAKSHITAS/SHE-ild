SHE-ild 🛡️
Your silent guardian. Your emergency ally.
A Women Safety Android App built using Kotlin + Jetpack Compose

SHE-ild is a proactive, offline-first women safety application designed to respond in times of distress with just a shake of your phone.

When you're in danger, there's no time to think — just act. SHE-ild helps you act fast, even without saying a word.

This app listens for urgent gestures like phone shaking and automatically sends out SOS messages and calls your emergency contact with your last known location.

🌟 Features
🔐 Emergency Contact Setup
Add and save a trusted contact who'll receive your SOS signals.

🌍 Offline Functionality
Works even when there's no internet. Your safety isn’t compromised.

📍 Location Sharing
Sends your last known location via SMS during emergencies.

📞 Auto Emergency Call
Automatically dials your trusted contact when triggered.

🤳 Shake Detection
A simple shake of your phone triggers the emergency response.

🎨 Jetpack Compose UI
Clean, modern, and responsive design with Material 3 support.

🔄 Background Service
Runs silently in the background, always ready without interfering.

📱 App Flow
Splash Screen → Welcomes user.

Mobile Number Input → Registers the user.

Emergency Contact Setup → Stores name & number securely.

Permissions → Request for location & contact access.

User Info → User enters their name.

Start/Stop Service → Toggle background monitoring for shake detection.

🛠️ Tech Stack
Language: Kotlin

UI: Jetpack Compose

Navigation: Navigation Component with lambda-based event handling

Permissions: Runtime permissions handling (Contacts, Location, Call, SMS)

Background Work: Services

Sensors: Accelerometer for shake detection

🔐 Permissions Used
CALL_PHONE – to make emergency calls

SEND_SMS – to send emergency texts

ACCESS_FINE_LOCATION – to fetch accurate location

READ_CONTACTS – to select emergency contact

Note: All permissions are only used during emergencies and are requested transparently. Your data stays on your device.

📦 How to Run
Clone the repository:

bash
Copy
Edit
git clone https://github.com/your-username/she-ild.git
Open in Android Studio

Build the project and run on a real device (sensor features need physical device)

Grant necessary permissions when prompted

You're all set to be protected!

💡 Future Enhancements
🔊 Voice-triggered SOS

📡 Live location tracking

🔐 Secure cloud backup of contacts

👩‍💻 Panic mode UI with decoy screen

🤝 Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you’d like to change.

Let’s build safety, together.

