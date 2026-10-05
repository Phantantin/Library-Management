# Troubleshooting

- `401`: token missing/expired/invalid. Sign in again and confirm `JWT_SECRET` is stable across backend instances.
- Startup error `Could not resolve placeholder 'JWT_SECRET'`: current code now generates an ephemeral key for local development. For stable sessions, copy `.env.example` to `.env` and set a secret of at least 32 bytes. Production must also set `JWT_REQUIRE_CONFIGURED_SECRET=true`.
- `403`: authenticated user lacks `ROLE_ADMIN`, does not own the record, or the Next BFF rejected a cross-origin mutation.
- Browser cannot call API: confirm Next is using the BFF path, Spring is on `NEXT_PUBLIC_API_URL`, and `CORS_ORIGINS` has exact origins without trailing slash.
- Reset email missing: check SMTP credentials and `FRONTEND_URL`; the API intentionally does not reveal whether an email exists.
- Payment remains processing: verify sandbox credentials, supported currency, smallest-unit amount, returned payment ID and Payment Link contents. Client redirect alone never completes a payment.
- Checkout rejected: confirm active subscription, plan limits, no overdue loans, available active book and requested days within the plan.
- Membership subscription fails before opening Razorpay: set `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` in the Render backend service, then redeploy. Use test credentials for sandbox testing; never put them in Vercel or Git. Confirm `FRONTEND_URL` is the deployed frontend URL for the payment return page.
- Build fails after moving folders: run npm commands from sibling `frontend`, Maven commands from `Library-Management`.
