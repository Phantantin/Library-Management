# Environment reference

Backend variables: `PORT`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_MAX_POOL_SIZE`, `DDL_AUTO`, `FLYWAY_ENABLED`, `FLYWAY_BASELINE_ON_MIGRATE`, `JWT_SECRET` (at least 32 UTF-8 bytes), `JWT_REQUIRE_CONFIGURED_SECRET`, `FRONTEND_URL`, `BACKEND_PUBLIC_URL`, `CORS_ORIGINS`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, `VNPAY_TMN_CODE`, `VNPAY_HASH_SECRET`, `VNPAY_PAYMENT_URL`, `PAYMENT_CURRENCY`, and optional `ADMIN_EMAIL`/`ADMIN_PASSWORD`. Set `JWT_REQUIRE_CONFIGURED_SECRET=true`, `FLYWAY_ENABLED=true` and `DDL_AUTO=validate` outside local development.

Frontend variables:

- `NEXT_PUBLIC_API_URL`, normally `http://localhost:5000`. It is public configuration used by the Next server to reach Spring.
- `UPLOADTHING_TOKEN`, the private UploadThing v7 server token used to sign admin cover uploads. Store it only in `.env.local` or the deployment secret store; never prefix it with `NEXT_PUBLIC_`.

VNPAY accepts VND only. Payment amounts are stored as whole VND and multiplied by 100 only when constructing the VNPAY request. Membership plans with another currency cannot be purchased through VNPAY; update each plan to its intended VND price in Admin → Subscriptions. Existing plan prices are deliberately not converted automatically.
