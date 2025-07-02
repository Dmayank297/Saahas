<h1 align="center">🚨 Saahas - Women Safety App</h1>

<p align="center">
  <b>A native Android app for real-time women safety escalation and emergency response.</b><br>
  Designed with Kotlin, Jetpack Compose, and Firebase — with a minimal dark UI.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Built%20With-Kotlin-blueviolet?style=flat-square&logo=kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-34a853?style=flat-square&logo=android" />
  <img src="https://img.shields.io/badge/Backend-Node.js%20%26%20MongoDB-green?style=flat-square&logo=node.js" />
  <img src="https://img.shields.io/badge/Database-Room%20%26%20green?style=flat-square&logo=Room" />
  <img src="https://img.shields.io/badge/Auth-Firebase-orange?style=flat-square&logo=firebase" />
</p>

---

## 🔍 Overview

**Saahas** empowers women with multi-level safety services through voice, location, wearable tech, and real-time triggers — all accessible from a single app.

It works under **3 Emergency Levels**:

- **Level 1**: SOS messages, emergency calls, nearest hospitals — even when the phone is locked.
- **Level 2**: Live location sharing, voice commands, shake detection, inactivity checks.
- **Level 3**: Critical escalation with audio/video recording, alert to nearby users, and authorities.

---

## 📱 Core Features

### 🔐 Level 1 – Basic (Phone may be locked)
- One-tap **SOS Button** sends live location to emergency contacts.
- Long-press triggers an **emergency call**.
- Nearby **hospital detection** via Google Maps.

### 📡 Level 2 – Smart Triggers
- Live location sharing with cancel anytime option.
- Voice recognition (multi-language) in background.
- Triggers if:
    - No response to periodic check-ins.
    - Abnormal device motion (shake/fall detection).

### 🚨 Level 3 – Emergency Response
- Location broadcast to nearby Saahas users.
- SOS to police stations and hospitals.
- Audio/video recording begins (stored securely).
- Emergency buzzer activates.
- Secret code / gesture cancels escalation.

---

## 🎛️ Other Services

- **Volume Button**: Triggers immediate SOS.
- **Shake Detection**: Starts voice/camera recording.
- **Voice Commands**: “Call Police”, “Share Location”, etc.
- **Fake Call**: Escape strategy.
- **Wearable Integration**: Trigger escalation from devices.
- **Offline Sound Detection**: Recognizes buzzer in absence of internet.

---

## 🛠️ Tech Stack

| Layer        | Tools & Libraries                                  |
|--------------|----------------------------------------------------|
| **UI**       | Jetpack Compose, Material 3, Custom animations     |
| **Backend**  | Node.js (Express.js) + MongoDB (Cloud)             |
| **Local DB** | Room Persistence Library                           |
| **Auth**     | Firebase Authentication (Email, Google)            |
| **APIs**     | Google Maps, SpeechRecognizer, Sensors, SMSManager |

---

## 🖼️ Screenshots

```markdown
### Home Screen
![home](https://github.com/user-attachments/assets/60bd8250-7e78-44c1-891b-222935ba4daf)

### Map Screen
![map](https://github.com/user-attachments/assets/ada50e09-3c73-477a-aa76-eba8c785a2c4)

### Emergency Triggers
![Emergency_Options](https://github.com/user-attachments/assets/7fc0ffbc-d719-4045-ae47-35341178e074)

### Emergency Info
![emergency1](https://github.com/user-attachments/assets/d28e1995-efe7-418b-a558-eec0ffd583e0)

![emergency2](https://github.com/user-attachments/assets/0f7a2c8e-03d6-4cc0-8561-a4ed7b074748)

### Voice System
![voice](https://github.com/user-attachments/assets/9db6da33-6d0f-4b40-a316-705abd77c56e)

```
## 🚀 Getting Started

### 🔧 Prerequisites

- Android Studio (ladybug or newer)
- Kotlin 1.9+
- Firebase Project with Authentication enabled
- Google Maps API Key

### ⚙️ Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Dmayank297/Saahas.git
   cd Saahas



