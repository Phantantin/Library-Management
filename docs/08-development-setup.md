# Development setup

Requirements: Java 17+, Node 22+, npm and MySQL 8+. From `Library-Management`, copy `.env.example` to the ignored `.env`, replace its local values, create `library_db`, then run `./mvnw spring-boot:run` (Windows: `.\mvnw.cmd spring-boot:run`). Spring imports this root `.env` as properties and listens on port 5000. If `JWT_SECRET` is omitted locally, Spring generates an ephemeral development key and invalidates sessions on every restart.

From sibling `frontend`, copy `.env.example` to `.env.local`, set `NEXT_PUBLIC_API_URL` and the private UploadThing v7 token, run `npm install`, then `npm run dev`. Next listens on port 3000, forwards same-origin browser requests to `NEXT_PUBLIC_API_URL`, and exposes the admin-protected cover upload route at `/api/uploadthing`.

Optional bootstrap admin creation requires both `ADMIN_EMAIL` and a password of at least 12 characters. Remove those variables after first successful creation. Existing database credentials can be supplied with standard `SPRING_DATASOURCE_*` environment variables.
