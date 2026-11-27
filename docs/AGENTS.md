# Instructions for AI Coding Agents

---

## 🤖 Directives for Autonomous AI Agents

Welcome to **VIBE**. This project is a production-grade native Android music player frontend built with Jetpack Compose, Material 3, and AndroidX Media3 ExoPlayer.

Whenever you work on this repository, you **MUST** follow these mandatory rules:

---

## 📌 Rule 1: Continuous Markdown Documentation

Do **NOT** delay documentation until the end. After every task, update:
- `docs/PROJECT_STATE.md` with current feature status, files changed, and build status.
- `docs/TASKS.md` marking completed items and noting next steps.
- `docs/ARCHITECTURE.md` if components or patterns are added.
- `docs/DECISIONS.md` whenever an architectural or technical choice is made.
- `docs/SESSION_HANDOFF.md` before concluding any session.

---

## 📌 Rule 2: Preserve Existing Work

- Do **NOT** delete or wipe out working components, theme tokens, or mock datasets unless explicitly refactoring.
- Maintain backwards compatibility with the existing UI state flow.

---

## 📌 Rule 3: Native Android Only

- Do **NOT** create web, React, React Native, Flutter, or HTML/JS files.
- All code must be idiomatic Kotlin inside `app/src/main/java/com/vibe/music/`.

---

## 📌 Rule 4: Decoupled Architecture

- All data retrieval must go through repository interfaces in `com.vibe.music.data.repository`.
- Keep ViewModels dependent only on interfaces, never concrete mock classes directly.
- The player engine must stay encapsulated inside `MusicPlaybackManager`.

---

## 🛠️ Build Commands

```bash
export ANDROID_HOME=/home/codespace/android-sdk
export JAVA_HOME=/usr/local/sdkman/candidates/java/21.0.13.0+2-ms
./gradlew assembleDebug
```
Ensure the debug APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`
