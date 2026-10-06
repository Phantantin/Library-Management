# Troubleshooting

- `401`: token missing/expired/invalid. Sign in again and confirm `JWT_SECRET` is stable across backend instances.
- Startup error `Could not resolve placeholder 'JWT_SECRET'`: current code now generates an ephemeral key for local development. For stable sessions, copy `.env.example` to `.env` and set a secret of at least 32 bytes. Production must also set `JWT_REQUIRE_CONFIGURED_SECRET=true`.
- `403`: authenticated user lacks `ROLE_ADMIN`, does not own the record, or the Next BFF rejected a cross-origin mutation.
- Browser cannot call API: confirm Next is using the BFF path, Spring is on `NEXT_PUBLIC_API_URL`, and `CORS_ORIGINS` has exact origins without trailing slash.
- Reset email missing: check SMTP credentials and `FRONTEND_URL`; the API intentionally does not reveal whether an email exists.
- Payment remains processing: confirm VNPAY IPN is registered to the exact public HTTPS backend URL, Render has the correct merchant code/signing secret, the plan uses VND, and the provider callback amount matches. Browser Return alone never completes a payment.
- Checkout rejected: confirm active subscription, plan limits, no overdue loans, available active book and requested days within the plan.
- Checkout does not open: set `VNPAY_TMN_CODE`, `VNPAY_HASH_SECRET`, `VNPAY_PAYMENT_URL`, `BACKEND_PUBLIC_URL` and `FRONTEND_URL` in the Render backend service, then redeploy. Keep the secret only in Render. Confirm the selected membership plan is priced in VND.
- Payment never becomes successful: inspect Render logs for `/api/payments/vnpay/ipn`, ask VNPAY to register `https://YOUR-SERVICE.onrender.com/api/payments/vnpay/ipn`, and check the `RspCode` response. Do not manually activate a plan from a browser Return.
- Build fails after moving folders: run npm commands from sibling `frontend`, Maven commands from `Library-Management`.
