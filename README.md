# 🏛️ Grama-Angana — Community Space Manager

A GenAI-powered Android app that digitises community hall management for rural villages in India. Citizens can view booking availability, request hall slots, fund maintenance repairs, and check today's events — all in one place.

## 🎯 Problem Statement
Community halls (Anganas/Samudaya Bhavanas) in rural India remain empty and fall into decay because:
- Citizens don't know the booking status or who the key holder is
- No system exists to request or approve hall usage
- Double bookings and favoritism are common
- Small repairs go unfunded due to no community funding system

## ✨ Features
- 📅 **Hall Booking Calendar** — View Booked vs Free slots in real time
- ❌ **Double Booking Prevention** — System blocks duplicate bookings automatically
- 🤖 **Gemini AI Suggestions** — Smart time slot recommendations based on booking purpose
- 🔧 **Maintenance Jar** — Crowdfund small repairs with visual progress bars
- 📢 **Live Event Board** — See what's happening at the hall today
- 🔥 **Firebase Real-time Sync** — All bookings sync instantly across devices
- 💾 **Room DB** — Maintenance items stored locally for offline access

## 🛠️ Tech Stack
- **Language:** Kotlin
- **Platform:** Android (min SDK 26)
- **Database:** Firebase Realtime Database + Room DB (SQLite)
- **AI:** Google Gemini Pro API
- **UI:** Material Design, CalendarView, CardView
- **Architecture:** Fragment-based navigation with Bottom Nav

## 📂 Project Structure
