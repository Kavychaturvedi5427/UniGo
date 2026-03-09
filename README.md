# UniGo

UniGo is a comprehensive university companion Android application designed to help students manage their academic life efficiently. The application centralizes essential academic tools such as assignment tracking, exam scheduling, and digitized note storage into a single platform.

UniGo integrates modern Android development practices with cloud services to provide a reliable and scalable academic management solution.

---

# Features

## Authentication & Profile
- Secure login and registration using Firebase Authentication
- Password recovery functionality
- User profile management with editable personal details

## Notes Management
- Upload handwritten or printed notes as images
- Cloud storage integration using Cloudinary
- Grid-based UI for organized browsing
- Full-screen image viewing with zoom support using PhotoView
- Integration-ready support for text recognition using Google ML Kit

## Academic Tracking

### Assignments
- Create and manage assignments
- Track deadlines and descriptions
- Organized assignment listing

### Exams
- Maintain a dedicated exam schedule
- Easily view upcoming examinations

### Attendance
- Monitor attendance status
- Track attendance records

## Automation & Background Tasks
- Background workers for automated checks
- Assignment reminder notifications
- Attendance monitoring tasks
- Daily motivational quotes delivered through notifications

---

# Tech Stack

## Language
- Java

## Architecture
- MVVM 
- Feature-based modular architecture with separation of concerns across UI, data, API, and worker layers.

## UI / UX
- Material Design Components
- View Binding
- Lottie Animations
- Glide (image loading)
- PhotoView (zoomable image viewer)

## Backend & Storage
- Firebase Authentication
- Firebase Firestore
- Cloudinary (image storage)

## Networking
- Retrofit
- OkHttp

## Machine Learning
- Google ML Kit (Text Recognition)

## Background Processing
- WorkManager for reliable background task scheduling

---

# Project Structure

```
app
 ┣ manifests
 ┃ ┗ AndroidManifest.xml
 ┣ java/com.kavya.unigo
 ┃ ┣ api
 ┃ ┣ data
 ┃ ┣ responseModel
 ┃ ┣ ui
 ┃ ┃ ┣ about
 ┃ ┃ ┣ auth
 ┃ ┃ ┣ features
 ┃ ┃ ┃ ┣ Assignment
 ┃ ┃ ┃ ┣ EditProfile
 ┃ ┃ ┃ ┣ Exams
 ┃ ┃ ┃ ┣ Notes
 ┃ ┃ ┃ ┗ workers
 ┃ ┃ ┣ landing
 ┃ ┃ ┣ settings
 ┃ ┃ ┗ splash
 ┃ ┣ utils
 ┃ ┗ MyApplication
```

This modular structure separates responsibilities across different packages to improve maintainability and scalability.

---

# Core Feature Workflows

## Application Launch Workflow

1. The application starts from the **Splash Screen**
2. The system checks the authentication state
3. If the user is authenticated:
   - The user is redirected to the **Landing / Dashboard screen**
4. If not authenticated:
   - The user is redirected to the **Login / Signup screen**

---

## Authentication Workflow

1. User enters email and password
2. Firebase Authentication validates the credentials
3. If authentication succeeds:
   - A user session is created
   - User is redirected to the dashboard

---

## Notes Upload Workflow

1. User navigates to the **Notes section**
2. User selects an image of handwritten notes
3. The image is uploaded to **Cloudinary**
4. The image URL is saved in **Firebase Firestore**
5. The note appears in the grid-based notes list
6. User can open the image with **PhotoView zoom support**

---

## Assignment Management Workflow

1. User opens the **Assignments section**
2. User creates a new assignment
3. Assignment data includes:
   - Title
   - Description
   - Deadline
4. The assignment is stored in **Firebase Firestore**
5. The assignment list updates automatically

---

## Background Notification Workflow

1. WorkManager schedules periodic background workers
2. Workers check for:
   - Assignment deadlines
   - Attendance status
3. If conditions are met:
   - A notification is generated using NotificationManager
4. The user receives a reminder notification

---

# Getting Started

## Prerequisites

- Android Studio Iguana or newer
- Java Development Kit (JDK) 17+
- Firebase Project
- Cloudinary Account

---

# Setup Instructions

## Clone the Repository

```bash
git clone https://github.com/Kavychaturvedi5427/UniGo.git
```

---

## Firebase Configuration

1. Create a Firebase project
2. Add an Android application
3. Download the configuration file

```
google-services.json
```

4. Place the file in:

```
app/google-services.json
```

5. Enable the following services:
- Firebase Authentication
- Firestore Database

---

## Cloudinary Setup

1. Create a Cloudinary account
2. Create an **unsigned upload preset**
3. Update configuration inside:

```
CloudinaryClient.java
```

---

## Build the Project

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run the application

---
# Screenshots
<div align="center">

### 🧭 App Overview
<table>
<tr>
<td align="center"><b>Splash Screen</b></td>
<td align="center"><b>Choose Auth</b></td>
<td align="center"><b>About App</b></td>
</tr>
<tr>
<td><img src="screenshots/SplahActivity.jpeg" width="250"/></td>
<td><img src="screenshots/ChooseAuth.jpeg" width="250"/></td>
<td><img src="screenshots/AboutApp.jpeg" width="250"/></td>
</tr>
</table>

### 🔐 Authentication Flow
<table>
<tr>
<td align="center"><b>Login Screen</b></td>
<td align="center"><b>Signup Screen</b></td>
<td align="center"><b>Settings</b></td>
</tr>
<tr>
<td><img src="screenshots/Login.jpeg" width="250"/></td>
<td><img src="screenshots/SignUp.jpeg" width="250"/></td>
<td><img src="screenshots/Settings.jpeg" width="250"/></td>
</tr>
</table>

### 📊 Dashboard
<table>
<tr>
<td align="center"><b>Main Dashboard</b></td>
</tr>
<tr>
<td><img src="screenshots/Dashboard.png" width="250"/></td>
</tr>
</table>

### 📝 Assignment Management
<table>
<tr>
<td align="center"><b>Assignments</b></td>
<td align="center"><b>Add Assignment</b></td>
</tr>
<tr>
<td><img src="screenshots/Assignment.jpeg" width="250"/></td>
<td><img src="screenshots/AddAssignment.jpeg" width="250"/></td>
</tr>
</table>

### 📚 Notes Management
<table>
<tr>
<td align="center"><b>Notes</b></td>
<td align="center"><b>Add Notes</b></td>
</tr>
<tr>
<td><img src="screenshots/Notes.jpeg" width="250"/></td>
<td><img src="screenshots/Addnotes.jpeg" width="250"/></td>
</tr>
</table>

</div>

---

# Future Improvements

- OCR based text extraction from notes
- Calendar integration
- Push notifications using Firebase Cloud Messaging
- Offline data caching
- Improved analytics and performance monitoring

---

# Contributing

Contributions are welcome. Feel free to open an issue or submit a pull request.

---

# License

This project is licensed under the MIT License.

---

Developed for improving student productivity.