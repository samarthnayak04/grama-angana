# 🏛️ Grama-Angana — Community Space Manager

An Android app that turns the village community hall (Angana / Samudaya Bhavana) into a Hub of Activity through a public booking calendar, crowdfunded maintenance system, and live event board.

## 📱 Features

- **🔐 Login / Signup** — Phone + password authentication, saved to Firebase
- **📋 Event Board** — Live events loaded dynamically from Firebase Realtime Database
- **📅 Hall Calendar** — Tap any date to see BOOKED/FREE status with time slot details
- **📝 Book the Hall** — Submit booking with date, start time, end time + AI-powered suggestions
- **📋 My Bookings** — View only your own bookings filtered by phone number
- **🔧 Maintenance Jar** — Crowdfund items (fans, chairs, bulbs) with progress bars (Room DB)
- **🔑 Key Holder Info** — Direct call to the Panchayat key holder
- **🤖 Gemini AI Integration** — Smart booking time suggestions based on event purpose

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Android XML layouts + Material Design |
| Real-time DB | Firebase Realtime Database |
| Local DB | Room Database |
| AI | Google Gemini API |
| Architecture | Fragment-based with Bottom Navigation |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 |

## 🎯 Success Criteria Met

- ✅ Calendar prevents Double Booking of the same date
- ✅ Maintenance Jar shows progress bar for funds/pledges
- ✅ Professional, community-focused UI

## 📂 Project Structure
app/src/main/
├── java/com/example/gramaangana/
│   ├── MainActivity.kt
│   ├── LoginActivity.kt
│   ├── EventBoardFragment.kt        (Firebase-driven)
│   ├── CalendarFragment.kt          (Firebase + time slots)
│   ├── BookingFragment.kt           (Firebase + Gemini AI)
│   ├── MyBookingsFragment.kt        (User-filtered bookings)
│   ├── MaintenanceFragment.kt       (Room DB)
│   ├── KeyHolderFragment.kt         (Firebase)
│   ├── MaintenanceItem.kt           (Room Entity, DAO, Database)
│   └── GeminiHelper.kt              (Gemini API wrapper)
└── res/
├── layout/   (10 XML layouts)
├── menu/     (bottom_nav_menu.xml)
└── values/   (themes.xml)

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Ladybug or later
- Android SDK 26+
- A Firebase project with Realtime Database enabled
- A Gemini API key from Google AI Studio

### Steps

1. Clone the repository
```bash
   git clone https://github.com/YOUR_USERNAME/grama-angana.git
   cd grama-angana
```

2. Add your Gemini API key in `local.properties` at project root:
   GEMINI_API_KEY=your_key_here

3. Open in Android Studio and click **Sync Now**

4. Run on a physical device or emulator (Android 8.0+)

## 🗄️ Firebase Database Structure
grama-angana-db/
├── users/
│   └── {phone_number}/
│       ├── name
│       ├── phone
│       └── password
├── bookings/
│   └── {auto_id}/
│       ├── name
│       ├── purpose
│       ├── date
│       ├── startTime
│       ├── endTime
│       ├── status (pending / approved / rejected)
│       └── userPhone
├── events/
│   └── {evt_id}/
│       ├── title
│       ├── desc
│       └── time
└── keyholder/
├── name
├── phone
├── role
├── address
└── timings

## 🤖 GenAI Integration

The app uses Google's Gemini API for **Smart Booking Suggestions**. When a user enters a booking purpose (e.g., "wedding reception", "cricket match"), tapping the AI button generates a contextual recommendation for the best time slot and tips — reducing back-and-forth with the Panchayat.



