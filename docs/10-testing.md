# Testing

Backend tests use in-memory H2 in MySQL compatibility mode and do not require local secrets or the developer database. Mapper tests cover the corrected genre/cover mapping, client-controlled IDs and due-date calculation. The Spring context test verifies repository queries and security wiring.

Frontend Vitest tests cover formatting, checkout-host allowlisting, authentication validation, copy-count invariants, review rules, English/Vietnamese translation behavior and the critical mutation hook's cache invalidation/error behavior. Quality commands are `npm test`, `npm run lint`, `npm run typecheck`, and `npm run build`.

Final automated audit on 2026-09-21: Spring Boot 11/11 tests passed, including registration/login/profile integration, analytics query execution and JWT configuration behavior; frontend 17/17 tests across 5 suites passed; ESLint and strict TypeScript passed with zero errors; the Next.js production build completed and generated all 32 configured routes, including the protected UploadThing cover-upload endpoint.

Live integration smoke test against the configured MySQL instance confirmed backend startup on port 5000, registration and login through the Next BFF, HttpOnly session-cookie handling, authenticated profile access, and bilingual server rendering. Domain unit/integration tests cover the remaining local logic. Real Razorpay and SMTP tests require sandbox credentials and external connectivity.
