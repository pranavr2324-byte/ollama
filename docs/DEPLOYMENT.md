# VIBE — Production Deployment Guide

---

## 📦 1. Backend Production Deployment

### Option A: Railway / Render / Fly.io (Docker or Node.js)

1. Build production bundle:
   ```bash
   cd backend
   npm install --production=false
   npx prisma generate
   npm run build
   ```

2. Set environment variables on the hosting platform:
   - `NODE_ENV=production`
   - `PORT=4000`
   - `DATABASE_URL=postgresql://postgres:[PASSWORD]@[HOST]:5432/postgres?sslmode=require`
   - `CLOUDINARY_CLOUD_NAME=[YOUR_CLOUD_NAME]`
   - `CLOUDINARY_API_KEY=[YOUR_API_KEY]`
   - `CLOUDINARY_API_SECRET=[YOUR_API_SECRET]`
   - `CLOUDINARY_MUSIC_FOLDER=vibe/music`
   - `JWT_SECRET=[STRONG_32_CHAR_SECRET]`
   - `CORS_ORIGIN=*`

3. Start command:
   ```bash
   node dist/server.js
   ```

4. Database migrations:
   ```bash
   npx prisma db push
   ```

---

## 📱 2. Android Production Build & Release

1. Configure the production backend URL in `VibeNetworkConfig.kt` or via build variants in `app/build.gradle.kts`.
2. Generate a release keystore:
   ```bash
   keytool -genkey -v -keystore vibe-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias vibe
   ```
3. Assemble release APK:
   ```bash
   ./gradlew assembleRelease
   ```
4. Or assemble debug APK for testing:
   ```bash
   ./gradlew assembleDebug
   # Artifact: app/build/outputs/apk/debug/app-debug.apk
   ```
