# Production deployment: Aiven → Render → Vercel

The project uses two Git repositories:

- Backend: `https://github.com/Phantantin/Library-Management` (`Library-Management/`).
- Frontend: `https://github.com/Phantantin/Library-Management-Frontend` (`frontend/`).

Do not use `Sass-Pos-Application` as a root directory. It belongs to a different project. Each repository has its application at the repository root.

## 1. Preflight

Both `.gitignore` files exclude `.env`, `.env.local` and local Spring properties. Never copy real Aiven, Razorpay, SMTP or UploadThing values into Git. The backend deployment files are `Dockerfile`, `.dockerignore`, `render.yaml` and `src/main/resources/db/migration/V1__initial_schema.sql`.

Run before pushing:

```powershell
cd Library-Management
.\mvnw.cmd test
.\mvnw.cmd test "-Dspring.flyway.enabled=true" "-Dspring.flyway.baseline-on-migrate=false" "-Dspring.jpa.hibernate.ddl-auto=validate"
.\mvnw.cmd clean package -DskipTests

cd ..\frontend
npm ci
npm run lint
npm run typecheck
npm test
npm run build
```

## 2. Aiven MySQL

Create an Aiven for MySQL service and wait for `Running`. Copy host, port, database, username and password from Connection information. Render uses:

```text
DB_URL=jdbc:mysql://HOST:PORT/DATABASE?sslmode=require
DB_USERNAME=the Aiven username
DB_PASSWORD=the Aiven password
```

Do not put credentials inside the URL when separate username/password variables are provided. The initial Flyway migration creates the complete schema. `FLYWAY_BASELINE_ON_MIGRATE=true` also makes the release compatible with a non-empty database that predates Flyway; Flyway records the existing schema as a baseline instead of replaying V1.

## 3. Render backend

Connect the backend GitHub repository. Either create a Blueprint from `render.yaml` or create a Web Service manually:

- Branch: the branch containing the deployment commit.
- Language/runtime: Docker.
- Root Directory: leave empty (`.`).
- Dockerfile Path: `./Dockerfile`.
- Plan: Free for demonstration.
- Health Check Path: `/health`.

Required environment variables:

| Key | Value |
| --- | --- |
| `PORT` | `5000` |
| `SERVER_PORT` | `5000` |
| `DB_URL` | Aiven JDBC URL with `?sslmode=require` |
| `DB_USERNAME` | Aiven username |
| `DB_PASSWORD` | Aiven password |
| `DB_MAX_POOL_SIZE` | `5` |
| `JWT_SECRET` | Render-generated value or random secret ≥ 32 bytes |
| `JWT_REQUIRE_CONFIGURED_SECRET` | `true` |
| `FLYWAY_ENABLED` | `true` |
| `FLYWAY_BASELINE_ON_MIGRATE` | `true` |
| `DDL_AUTO` | `validate` |
| `FRONTEND_URL` | Temporary `http://localhost:3000`, then the exact Vercel production URL |
| `CORS_ORIGINS` | Temporary `http://localhost:3000`, then the exact Vercel production URL |
| `PAYMENT_CURRENCY` | `VND` |
| `ADMIN_EMAIL` | Optional bootstrap admin email |
| `ADMIN_PASSWORD` | Optional bootstrap password, at least 12 characters |
| `RAZORPAY_KEY_ID` / `RAZORPAY_KEY_SECRET` | Required in Render before members can subscribe to paid plans or pay fines. Use Razorpay test keys for sandbox testing; keep both values only in Render secrets. |
| `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | Optional until reset email is tested |
| `SPRING_MAIL_PORT` | Provider port; Render Free blocks 25/465/587 |

After deploy, verify:

```text
GET https://YOUR-SERVICE.onrender.com/health
```

Expected JSON contains `"status":"UP"`. Record the Render URL for Vercel.

## 4. Vercel frontend

Import `Library-Management-Frontend`. Because Next.js is at repository root, keep Root Directory as `.` and use the detected Next.js preset. Add these variables to Production (and Preview if previews should work):

| Key | Value |
| --- | --- |
| `NEXT_PUBLIC_API_URL` | Exact Render origin, for example `https://YOUR-SERVICE.onrender.com` |
| `UPLOADTHING_TOKEN` | Private UploadThing server token |

`UPLOADTHING_TOKEN` must not have the `NEXT_PUBLIC_` prefix. Deploy and record the canonical production URL, for example `https://PROJECT.vercel.app`.

## 5. Close the CORS/callback loop

Return to Render and replace the temporary values:

```text
FRONTEND_URL=https://PROJECT.vercel.app
CORS_ORIGINS=https://PROJECT.vercel.app
```

Do not add a trailing slash. Redeploy Render. Preview deployments have changing hostnames; add only specific preview origins when needed rather than using `*` with credentials.

## 6. Production smoke test

1. Open `/books` and confirm catalog requests reach Render.
2. Register a new user, sign out and sign in again.
3. Confirm `/dashboard` loads and `/admin` rejects a normal user.
4. Sign in with the bootstrap admin, create a genre and upload/create a book.
5. Verify search, book details and the uploaded cover.
6. Test checkout only after creating/activating an appropriate subscription.
7. Add `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` to Render, use sandbox credentials first, and confirm the backend verification result. Without these keys, paid membership signup and fine payment return HTTP 503 and remain unavailable.
8. Test forgot-password only after configuring an email route available from Render.

## 7. Free-tier behavior

Render Free sleeps after 15 minutes without inbound traffic; the next request can take about one minute. Its filesystem is ephemeral, which is why images use UploadThing and business data uses Aiven. Render Free also blocks outbound SMTP ports 25, 465 and 587. These platform limits should be stated in demonstrations.
