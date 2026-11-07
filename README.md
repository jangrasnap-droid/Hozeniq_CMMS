# CMMS Android App (APK)

This project wraps the CMMS web app (`app/src/main/assets/index.html`) in an Android app.
Everything the CMMS does works inside the app: Machine Master, Breakdown log,
Scan & Log Breakdown with the phone camera, QR sheet downloads, printing, and Excel exports.

## 1. Build the APK (Android Studio)

1. Install **Android Studio** (Koala or newer). It includes the JDK and Android SDK.
2. Open Android Studio, choose **Open**, and select this `CMMS_Android` folder.
3. Wait for the first Gradle sync to finish. It downloads the libraries, so it needs internet.
4. Menu **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.
5. The file is created at:
   `app/build/outputs/apk/debug/app-debug.apk`

## 2. Install on a phone

- Copy `app-debug.apk` to the phone, open it, and allow "Install unknown apps" when asked.
- Or connect the phone with USB debugging on and run **Run > Run 'app'** in Android Studio.

## 3. Release APK for distribution (signed)

Menu **Build > Generate Signed App Bundle / APK > APK**. Create a keystore once and keep it safe.
Use the same keystore for every future update, or the phones will refuse the update.

## 4. Settings you may want to change

- `app/build.gradle.kts`: `applicationId` (package name), `versionCode`, `versionName`.
- App icon: right-click `app > res > New > Image Asset` to replace the placeholder icon.
- App name: `app/src/main/res/values/strings.xml`.

## 5. Offline scanning (recommended for the plant floor)

The QR scanner library loads from the app first and from the internet only as a fallback.
To make scanning work without internet, download the file and place it here:

`app/src/main/assets/jsQR.min.js`
(source: https://cdnjs.cloudflare.com/ajax/libs/jsQR/1.4.0/jsQR.min.js)

## 6. Things to know

- **Internet:** The screens use Tailwind, Font Awesome and Chart.js from the internet
  (the same as the browser version). Keep internet on for the first load on each phone.
- **Data is stored on each phone.** Breakdowns, work orders and machine data entered on a phone
  stay on that phone. They are not shared with the computer version or with other phones.
  Use the Excel exports to move data between them.
- **Camera:** The app serves its pages over a secure address, so the QR camera works.
  If the camera is unavailable, use "Take a photo of the QR" or type the machine code.
- **Downloads** (Excel exports, QR sheets) are saved to the phone's **Downloads** folder.
- **Printing** uses the phone's print service. Choose a printer, or "Save as PDF".

## 7. Structure

```
app/src/main/
  AndroidManifest.xml              permissions: camera, internet
  java/com/dharmvir/cmms/
    MainActivity.kt                full-screen WebView, camera, file picker, dialogs
    AndroidSaver.kt                bridge: save downloads, print
  assets/index.html                the CMMS web app
  res/                             app name, colours, icon
```
