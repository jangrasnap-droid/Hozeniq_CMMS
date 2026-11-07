# HOZENIQ CMMS Android Project - Audit Report

Date: 2026-11-07

## Results

- Project layout: standard Android Gradle project layout is present (`app/src/main/...`).
- XML: all 7 XML files parsed successfully.
- HTML/JavaScript: 22 inline JavaScript blocks in `app/src/main/assets/index.html` passed Node.js syntax checks.
- Gradle configuration: Android Gradle Plugin 8.5.2, Kotlin Android plugin 1.9.24, and Gradle 8.7 in GitHub Actions are a compatible configuration.
- Android manifest: launcher activity is exported; INTERNET and CAMERA permissions are declared; camera hardware is optional; legacy write permission is capped at API 28.
- WebView: app assets are served through `WebViewAssetLoader` at the HTTPS appassets origin; JavaScript and DOM storage are enabled; camera permission requests and file selection are handled.
- Download/print bridge: `AndroidSaver` provides download and print methods called by the HTML app.

## Important workspace issue

The screenshots showed shell commands pasted into `themes.xml` (the editor tab was marked modified). The uploaded ZIP contains a valid clean `app/src/main/res/values/themes.xml`. Restore that file from this clean project copy; do not paste terminal commands into XML files.

## Items not verified

A real Android APK build and device-level tests could not be run in the audit environment because it has no installed Gradle executable or Android SDK (`ANDROID_HOME`/`ANDROID_SDK_ROOT` unset). Therefore this report does not claim that compilation or runtime testing has passed.

## Build verification steps

1. Open the `CMMS_Android` folder in Android Studio.
2. Use JDK 17 and install Android SDK Platform 34 / Build Tools when prompted.
3. Allow Gradle sync to complete.
4. Run `gradle assembleDebug` from the project root, or use Android Studio's Build > Build APK(s).
5. Install `app/build/outputs/apk/debug/app-debug.apk` on a test phone.
6. Test first launch, local data persistence, camera permission, QR scanning, photo picker, downloads, printing, and external network-dependent features.

## Known operating constraints

- The HTML app loads Tailwind, Chart.js, Font Awesome, Google Fonts, and some optional libraries from CDNs. Those features need internet access unless the dependencies are bundled locally.
- CMMS data is stored locally on each device unless the configured shared backend is set up. Separate phones do not automatically share records.
- A signed release APK requires a release keystore. Keep the keystore and passwords secure and reuse the same signing key for updates.
