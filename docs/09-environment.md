# Environment reference

Backend variables: `PORT`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_MAX_POOL_SIZE`, `DDL_AUTO`, `FLYWAY_ENABLED`, `FLYWAY_BASELINE_ON_MIGRATE`, `JWT_SECRET` (at least 32 UTF-8 bytes), `JWT_REQUIRE_CONFIGURED_SECRET`, `FRONTEND_URL`, `CORS_ORIGINS`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET`, `PAYMENT_CURRENCY`, and optional `ADMIN_EMAIL`/`ADMIN_PASSWORD`. Set `JWT_REQUIRE_CONFIGURED_SECRET=true`, `FLYWAY_ENABLED=true` and `DDL_AUTO=validate` outside local development.

Frontend variables:

- `NEXT_PUBLIC_API_URL`, normally `http://localhost:5000`. It is public configuration used by the Next server to reach Spring.
- `UPLOADTHING_TOKEN`, the private UploadThing v7 server token used to sign admin cover uploads. Store it only in `.env.local` or the deployment secret store; never prefix it with `NEXT_PUBLIC_`.

Amounts are stored and sent to Razorpay in the smallest currency unit, matching the provider contract. VND therefore has zero fractional digits; INR has two. Plan/fine administration labels this explicitly. Do not mix currencies in one deployment without adding per-fine currency storage.
