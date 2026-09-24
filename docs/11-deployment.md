# Deployment

Build Spring with `./mvnw clean package` and run the generated jar with production environment variables. Set a stable `JWT_SECRET` of at least 32 bytes and `JWT_REQUIRE_CONFIGURED_SECRET=true`. Use a managed MySQL database and migrate schema with a migration tool before multiple production instances; `ddl-auto=update` is appropriate only for development.

Build Next from `frontend` with `npm ci && npm run build`, then run `npm start`. Set `NEXT_PUBLIC_API_URL` to the internal/public Spring URL available to the Next server and store `UPLOADTHING_TOKEN` as a private server environment variable. Set `FRONTEND_URL` to the public HTTPS frontend and `CORS_ORIGINS` to its exact origin. Secure session cookies activate automatically in production.

Terminate TLS at the platform/load balancer. Keep database, SMTP, JWT and Razorpay secrets in the platform secret store. Preserve sticky behavior only if introduced later; current auth is stateless. Razorpay callback URLs must be reachable over HTTPS.
