# VIBE Backend — Cloudinary Music Storage & Supabase API

Production-grade TypeScript / Express backend for VIBE, featuring Cloudinary cloud audio storage, Supabase PostgreSQL with Prisma ORM, JWT authentication, and REST APIs for music catalog, streaming, playlists, favourites, recently played, and administrative uploads.

---

## 🚀 Quick Start

### 1. Install Dependencies
```bash
cd backend
npm install
```

### 2. Configure Environment
```bash
cp .env.example .env
# Edit .env with your Cloudinary and Supabase PostgreSQL credentials
```

### 3. Generate Prisma Client & Migrate
```bash
npx prisma generate
npx prisma db push
```

### 4. Run Development Server
```bash
npm run dev
# Server listening at http://localhost:4000/api/v1
```

### 5. Run Tests
```bash
npm test
# 22 tests passing across auth, catalog, streaming, library, admin, and Cloudinary
```

---

## 📂 Architecture

- `src/config/`: Environment validation (Zod), Cloudinary SDK, Prisma client, Supabase client.
- `src/controllers/`: Auth, Catalog, Admin, and Library (Playlists, Favourites, Recently Played).
- `src/middleware/`: JWT & Supabase auth verification, Admin role guard, Multer audio disk upload, Error handling.
- `src/routes/`: Express routers mounted on `/api/v1`.
- `src/services/`: `CloudinaryService` for stream uploads, HTTPS delivery URL generation, asset metadata, and cleanup.
- `src/validators/`: Zod schemas for input validation.
- `tests/`: Vitest test suite with mocked Prisma and Cloudinary, plus integration tests.

For complete API documentation, see [docs/API_REFERENCE.md](../docs/API_REFERENCE.md).
