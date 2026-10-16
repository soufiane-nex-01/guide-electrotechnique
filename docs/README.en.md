# Guide des Métiers de l’Electrotechnique — NEX-01

[Français](../README.md) · [العربية](README.ar.md)

[Download Android APK](https://github.com/soufiane-nex-01/guide-electrotechnique/releases/download/v1.4.0/Guide-Electrotechnique-Android.apk) · [Download for Windows](https://github.com/soufiane-nex-01/guide-electrotechnique/releases/download/v1.4.0/Guide-Electrotechnique.zip) · [Release files](https://github.com/soufiane-nex-01/guide-electrotechnique/releases/tag/v1.4.0)

An offline educational application for learning about electrical engineering, industrial systems and related trades. **NEX-01** created the modern application to make these subjects accessible to the general public. French is the reference language, with Arabic RTL and English support, and light/dark themes.

## Version 1.4.0

| Area | Included |
| --- | --- |
| Library | 615 entries, 89 chapters, 7 domains |
| Rebuilt activities | 41 native HTML/SVG/JavaScript activities: 39 based on readable archive material and 2 clearly identified educational alternatives |
| Extra labs | 7 experiments covering basic circuits, faults, AC power, latching, 4–20 mA and ideal PWM/buck conversion |
| Learning paths | 6 paths, 36 lessons, 72 corrected questions |
| Practical assessment | 12 variable questions per session, explanations and local history |
| Glossary | 48 French terms with translated terms and definitions |
| Preserved archive | 769 original SWFs and 8 PDFs |

Search, filtering, bookmarks, reading progress and assessments work locally. The Android host is Java with WebView; the UI uses HTML, CSS and JavaScript. Windows uses a C#/.NET Framework local launcher. New simulations do not load Ruffle; drawings and calculations update on user input, and reading data is loaded on demand.

## Run or build

Install the Android APK on Android 9 or later with a recent WebView. Extract the complete Windows ZIP and open the launcher with its adjacent `app` directory. To compile from source, see [BUILD.md](BUILD.md). Progress is stored on the device and is removed when its application/browser data is cleared. External reference links open in the browser.

## Verification and limitations

The release passed 642 programmatic checks and scenario groups. All 3,320 Android Web assets were compared with the sources, and APK signing, alignment and archive integrity were checked. See the [verification report](VERIFICATION.md). DOM checks use happy-dom, not an actual Android WebView.

This release has not been tested on a physical phone or emulator; itel S18 performance and full visual verification remain outstanding. Archive translations are assisted and only partly reviewed. The 36 added lessons and their 72 questions received Arabic/English review. Unrebuilt historical diagrams and PDFs retain their original language. Four source references are missing and two SWFs are unreadable; the app identifies these cases.

Simulations use simplified educational models. Assessment results do not grant professional electrical authorization. Historical diagrams and protection curves do not replace current requirements or manufacturer documentation.

Historical material is attributed to **Philippe Juguet / POS Industry (2004–2005)**. Modernization is presented under **NEX-01**. New explanations reference Sitelec, INRS, OpenStax, Modbus Organization, Texas Instruments and NI. See [attribution and rights](../ATTRIBUTION.md); no blanket license is applied to third-party content.
