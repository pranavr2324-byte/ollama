# VIBE — Backend Setup Guide

---

## 🛠️ Prerequisites

- **Node.js** 20+ (verified on v24.22.2)
- **npm** 10+
- **Supabase PostgreSQL** database (or local PostgreSQL 15+)
- **Cloudinary** account (for cloud music storage & streaming)

---

## ⚙️ Environment Setup

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```

2. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```

3. Configure all required values in `backend/.env`:
   - `DATABASE_URL` — PostgreSQL connection string from Supabase (`Settings → Database → Connection String`).
   - `SUPABASE_URL`, `SUPABASE_ANON_KEY`, `SUPABASE_SERVICE_ROLE_KEY` — From Supabase `Settings → API`.
   - `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` — From Cloudinary Console.
   - `JWT_SECRET` — Strong random string (at least 32 characters).

---

## 📦 Installation & Database Migration

```bash
cd backend
npm install
npx prisma generate
npx prisma db push
```

---

## 🚀 Running the Server

### Development Mode (Hot Reload)
```bash
npm run dev
```

### Production Build
```bash
npm run build
npm start
```

---

## 🧪 Running Tests

```bash
# Unit & API tests (uses mocked Cloudinary and Prisma)
npm test

# Optional real Cloudinary upload integration test (requires valid .env credentials)
npm run test:cloudinary
```
