# Deployment

The production path is Aiven MySQL → Render Spring Boot → Vercel Next.js. The repository includes a versioned Flyway schema, a multi-stage backend `Dockerfile`, `render.yaml`, `/health`, and frontend `vercel.json`. Follow the exact checklist in [13-production-deployment.md](13-production-deployment.md).

Production enables Flyway and uses `DDL_AUTO=validate`; local development keeps the existing defaults until its database has been baselined. Set a stable `JWT_SECRET` of at least 32 UTF-8 bytes and `JWT_REQUIRE_CONFIGURED_SECRET=true`.

Render Free spins down when idle and blocks outbound SMTP ports 25, 465 and 587. Password-reset email therefore needs a provider that offers an allowed port such as 2525, an HTTP mail API, or a paid host. This does not affect authentication, catalog or the other API flows.
