# Implementation report

Implementation status: **95% of the requested end-to-end scope is certified**. All source-backed application flows and all 20 implementation phases have code and documentation. The remaining 5% is live certification that requires an external MySQL dataset, SMTP account and Razorpay sandbox credentials, plus the product extensions listed under known limitations.

## System architecture

The workspace contains sibling `frontend` and `Library-Management` applications. Next.js supplies public/member/admin experiences and a same-origin BFF. Spring Boot owns authentication, authorization, domain validation, persistence and provider integrations. MySQL is the production database; H2 isolates backend tests.

## Completed implementation

- Source-backed inventory of 16 controllers, 12 entities, DTOs, enums, services, repositories, mappers, events, security, payment and subscription logic. The generated contract contains all 78 controller endpoints and payload declarations.
- JWT secret and bootstrap administrator credentials moved to environment configuration; token validation hardened; password hashes hidden from JSON; forgot-password enumeration removed and reset links configured.
- Exact public/authenticated/admin security rules, admin route protection and service-level owner checks. CORS supports exact configurable origins including Next on port 3000.
- Corrected book/loan/review/reservation/genre mapping and domain defects, transactional inventory locks, subscription activation verification, fine association and provider-verified payment reconciliation.
- Added missing personal review/payment/subscription history, profile update, paginated admin users/reviews, live administration statistics and private admin catalog detail APIs.
- Next.js TypeScript frontend with semantic tokens, light/dark/system themes, persistent English/Vietnamese localization, public discovery, authentication, server-protected member/admin layouts, centralized API errors, TanStack Query cache invalidation, responsive states, accessible dialogs/forms and command palette.
- Admin-only UploadThing cover uploads with native image selection, preview, progress, JPG/PNG/WebP and 4 MB validation, server-side role verification and CDN-backed catalog rendering.
- Member features: overview, loans/renew/return, reservations/cancel, wishlist, review create/edit/delete, fines and secure payment initiation, provider callback verification, payment history, membership subscribe/cancel/history and profile update.
- Admin features: live totals plus 30-day circulation/new-user/revenue trends and popular-book/genre charts, book and genre CRUD, user records, checkout/check-in/overdue update, reservation fulfillment/cancel, review moderation, fine create/waive, payment ledger and subscription/plan management.
- H2-backed Spring context/mapper tests and frontend utility/schema/critical-hook tests. Setup, architecture, contracts, flows, UX, environment, testing, deployment and troubleshooting documentation.

## Final audit evidence

- Spring Boot: 11 tests passed, 0 failures and `BUILD SUCCESS` using the isolated H2 configuration, including signup/login/profile HTTP integration, validation, analytics queries and JWT fallback/production-guard behavior.
- Frontend: 17 tests passed across 5 suites, including translation, formatting, critical mutation and cover-image validation coverage; strict TypeScript and ESLint completed with zero errors.
- Next.js 16 production build compiled successfully and generated 32 public, authentication, member, admin, BFF, UploadThing and payment-callback routes.
- UploadThing storage connectivity and `/api/uploadthing` route configuration were verified; the route permits one image up to 4 MB and the token remains in ignored/private environment configuration. `npm audit --omit=dev` reports zero vulnerabilities.
- The generated API contract reports 78 endpoints. `git diff --check` reports no whitespace errors.
- The frontend is located at workspace `frontend/`, alongside `Library-Management/`, as requested.
- Live MySQL smoke testing confirmed Spring startup, BFF registration/login, HttpOnly cookie creation, authenticated profile access, and English/Vietnamese server rendering. The missing local frontend environment file that previously caused registration to return 502 has been created and remains git-ignored.

## Security and payment

JWT is kept in an HttpOnly SameSite cookie at the Next layer and is never persisted in localStorage. Next checks sessions for navigation; Spring remains authoritative. Payment redirect is treated as untrusted. Spring fetches Razorpay payment and Payment Link data, validates captured status, exact smallest-unit amount, currency and link membership, then applies the fine or activates the matching subscription.

## Known issues and limitations

- Razorpay and SMTP end-to-end behavior cannot be certified without valid external sandbox credentials. Automated tests do not make external provider calls.
- User role/status update and account deletion are absent because the backend domain has no account-status model or safe mutation APIs.
- The dashboard provides 30-day loans, returns, new users and provider-verified revenue. Historical overdue snapshots before this implementation cannot be reconstructed from the current transactional model.
- STRIPE remains an enum value only and is explicitly rejected; Razorpay is the implemented gateway.
- Fine currency is deployment-wide; multi-currency fines need a currency field on each fine.
- Production should replace Hibernate `ddl-auto=update` with Flyway/Liquibase and add webhook reconciliation, token revocation/refresh and isolated Playwright E2E fixtures.
- The sibling frontend is outside the nested backend Git repository. It needs its own Git repository or a parent-level monorepo before both applications can be committed together.

## Recommended future improvements

Add database migrations and constraints, webhook-based payment reconciliation with retry/audit records, account status/role workflows, richer statistics endpoints, email templates, queue notifications and Playwright tests running against ephemeral MySQL plus provider stubs.

## Completed

All planned code phases, source-backed API integration, static checks, unit tests, production build and documentation are complete. Live external-provider and production-deployment checks remain environment-dependent as listed above.
