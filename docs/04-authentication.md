# Authentication and authorization

Signup creates `ROLE_USER`. Login validates BCrypt and issues a 24-hour signed JWT containing email and authorities. The signing key comes from `JWT_SECRET`; there is no committed signing secret. For local development only, a missing secret produces a cryptographically random ephemeral key and a warning, so sessions expire after restart. Production must set `JWT_REQUIRE_CONFIGURED_SECRET=true`. The backend re-loads the user on every JWT request so role changes take effect without trusting stale client storage.

The Next BFF stores the token in `library_session`, an HttpOnly SameSite=Lax cookie. JavaScript cannot read it. Server layouts validate `/api/users/profile`; `/admin` requires `ROLE_ADMIN`. Spring Security independently guards all admin APIs and services verify ownership of personal records.

Forgot-password returns the same public response whether an account exists. Tokens are stored server-side, expire after five minutes and are deleted after successful reset. `FRONTEND_URL` builds the reset link. Logout deletes the frontend session cookie. The backend has no refresh-token or token-revocation model, so the current maximum session is 24 hours.
