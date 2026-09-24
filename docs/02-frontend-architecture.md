# Frontend architecture

The Next.js application is at `../frontend`, alongside this backend. It uses App Router, TypeScript strict mode, Tailwind CSS, Radix/shadcn-style primitives, TanStack Query, Axios, React Hook Form, Zod, Zustand, Lucide, Recharts and next-themes.

Public routes render catalog discovery. Member and admin layouts call the backend profile API on the server before rendering; admin routes additionally require `ROLE_ADMIN`. The Next route handler at `/api/backend/[...path]` is a same-origin BFF: login/signup responses place the JWT into an HttpOnly, SameSite cookie, strip it from browser-visible JSON, and forward it only to Spring. Mutations reject cross-origin requests. Spring remains the final authorization authority.

TanStack Query owns remote state and invalidates related queries after mutation. Zustand stores only mobile sidebar state. API types reflect Java DTOs; no production data is mocked. Shared states cover loading, error/retry, empty, disabled and mutation feedback. Desktop tables scroll on narrow screens; member/admin navigation becomes an accessible dialog on mobile.

The main folders are `app`, `components`, `features`, `hooks`, `lib`, `providers`, `schemas` and `types`. Domain API functions remain inside their features; route files stay thin.
