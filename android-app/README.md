# E-Waste Tracker - Native Android App (Java / MVVM)

Native Android mobile client in Java (minSdk 26, targetSdk 34) for the **E-Waste Recycling & Component Tracking Platform**, consuming the Spring Boot REST API.

---

## Prerequisites

1. **Spring Boot Backend**: Must be running on port 8080.
   - Start from repository root:
     ```bash
     mvn spring-boot:run
     ```
2. **Android Studio**: Jellyfish / Hedgehog / Iguana / Koala or newer with JDK 17+.
3. **Android SDK**: API level 26 minimum (defaults to API 34 platform).

---

## Opening and Running in Android Studio

1. Open **Android Studio**.
2. Click **Open** (or `File -> Open`) and select the directory:
   ```
   /home/ahmed/Projects/ewastetracking/android-app
   ```
3. Let Gradle sync dependencies.
4. Select a virtual device (e.g. Pixel 7 running API 33 or 34) and click **Run (Shift + F10)**.

---

## Backend Connectivity (Emulator vs Physical Device)

- **Android Emulator**:
  Uses `http://10.0.2.2:8080/api/` (configured in `app/build.gradle` as `BuildConfig.BASE_URL`).
  In Android emulators, `10.0.2.2` automatically routes to the host computer's `localhost`.
- **Physical Device over Wi-Fi**:
  Change `BASE_URL` in [`app/build.gradle`](app/build.gradle) to your computer's local network IP address, e.g.:
  ```groovy
  buildConfigField "String", "BASE_URL", "\"http://192.168.1.50:8080/api/\""
  ```

---

## Seed Accounts for Quick Testing

Use the 1-tap demo chips on the Login screen:
| Role | Email | Password | Dashboard Landing |
|------|-------|----------|-------------------|
| **Consumer** | `alice@example.com` | `pass123` | Consumer Portal (Drop-off, Submit, My Items, Credits) |
| **Facility Staff** | `staff@greentech.org` | `staff123` | Facility Operations (Intake Queue, Inventory) |
| **Business** | `contact@circularelectronics.com` | `biz123` | Circular Business Portal (Browse Parts, Requisitions) |
| **Admin** | `admin@ewaste.org` | `admin123` | Platform Governance |

---

## Command Line Build & Tests

From within `/android-app`:
```bash
# Run unit tests
./gradlew testDebugUnitTest

# Assemble debug APK
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
```
