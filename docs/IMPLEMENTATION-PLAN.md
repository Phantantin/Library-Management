# Implementation plan

0. Read backend, inventory contracts, run baseline checks — analysis complete; baseline checks running.
1. Record contracts and architecture — source-backed contract generated before frontend.
2. Repair security, mapping, ownership, transaction and payment gaps; add missing personal read APIs with documented contracts and regression tests.
3. Initialize Next.js App Router/TypeScript, semantic Tailwind/shadcn primitives, Query provider, Axios API, server session proxy, UI-only Zustand.
4. Implement auth and server-protected member/admin layouts; public catalog and detail with real APIs.
5. Implement member loans, reservations, wishlist, reviews, fines, payments, subscriptions and profile.
6. Implement admin catalog/forms, genre, user detail, circulation, reviews, fines, payment and subscription management; truthful analytics only.
7. Responsive/dark/accessibility/state audit; meaningful unit/integration tests; lint/typecheck/build.
8. Setup/deployment/troubleshooting docs and implementation report with exact evidence and remaining blockers.

Each phase retains working APIs where possible. No mock business data. No client-confirmed payment success. No destructive Git operations. Commits contain only reviewed source/docs/config templates.

## Architecture decisions

Frontend lives in the workspace-level `frontend/` directory, alongside the `Library-Management/` backend repository as requested. Next server handles an HttpOnly SameSite session cookie and forwards requests to Spring; browser never persists JWT in localStorage. Server layouts validate profile with Spring before rendering private routes and check ROLE_ADMIN. Axios talks to the same-origin BFF; NEXT_PUBLIC_API_URL configures Spring origin. TanStack Query owns server state. Zustand owns sidebar/preferences only. Forms use RHF and Zod, accessible dialogs and toasts. Backend remains authority on balances, statuses and eligibility.
