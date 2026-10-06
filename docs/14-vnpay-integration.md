# VNPAY Sandbox integration

The backend creates VNPAY hosted checkout URLs for membership purchases and fine payments. It supports the all-method checkout and VNPAY QR. VNPAY Sandbox is for test transactions only; after merchant approval, set the live merchant values and `VNPAY_PAYMENT_URL` to the production URL provided by VNPAY. The frontend permits only the official VNPAY Sandbox and production checkout hosts.

## Server configuration

Set these variables on the Spring backend service (Render), never in the browser or Git:

| Variable | Value |
| --- | --- |
| `VNPAY_TMN_CODE` | Merchant Terminal ID from the VNPAY Sandbox email |
| `VNPAY_HASH_SECRET` | Sandbox signing secret; store as a Render secret |
| `VNPAY_PAYMENT_URL` | `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html` |
| `BACKEND_PUBLIC_URL` | Public HTTPS origin of this Render service, without a trailing slash |
| `FRONTEND_URL` | Public frontend origin, without a trailing slash |

The backend constructs callback URLs from `BACKEND_PUBLIC_URL`:

| Purpose | URL |
| --- | --- |
| Return (browser) | `https://YOUR-RENDER-SERVICE.onrender.com/api/payments/vnpay/return` |
| IPN (server-to-server) | `https://YOUR-RENDER-SERVICE.onrender.com/api/payments/vnpay/ipn` |

Send the IPN URL to VNPAY support/merchant onboarding so they can register it. It must be publicly reachable over HTTPS. IPN is a GET endpoint and is exempt from member JWT authentication because VNPAY calls it directly; a valid HMAC-SHA512 signature, configured merchant code, local transaction reference, exact amount and successful provider response are required before it can update records.

## Payment flow

1. An authenticated member chooses a VND membership plan or an outstanding fine. The frontend posts to the existing subscription or fine endpoint with `paymentMethod=ALL` or `paymentMethod=QR`.
2. Spring calculates the authoritative price from its plan/fine record, creates a local `PROCESSING` payment and returns a signed HTTPS checkout URL. `QR` adds `vnp_BankCode=VNPAYQR`; `ALL` leaves the hosted page's method selection open.
3. VNPAY redirects the browser to the Return endpoint. Spring verifies the HMAC and amount, then redirects to the frontend result page. Return never marks the payment successful.
4. VNPAY calls the IPN endpoint. Spring validates the HMAC, merchant, local order reference, amount, response code and transaction status. On success it records the provider transaction number, marks fines paid or publishes the membership activation event, then acknowledges the callback. Repeated callbacks for terminal payments are safely acknowledged without reapplying effects.
5. The frontend reads the authenticated payment record and waits briefly for IPN. The database status is the source of truth; a query parameter or browser redirect cannot grant membership or clear a fine.

VNPAY amounts are VND whole-unit values in this app and are multiplied by 100 for VNPAY's request format. VNPAY accepts VND only. Existing subscription plan prices/currencies are not changed by the migration; verify and explicitly update existing plans to their intended VND amounts in Admin → Subscriptions before purchase. New plans default to VND.

## API contract

| Endpoint | Authentication | Behavior |
| --- | --- | --- |
| `POST /api/subscriptions/subscribe` | Member | JSON `{ "planId": 1, "paymentMethod": "QR" }`; creates inactive subscription and returns `PaymentInitiateResponse` with `paymentId` and `checkoutUrl`. |
| `POST /api/fines/{id}/pay?paymentMethod=QR` | Owner or admin | Creates a fine payment and returns `PaymentInitiateResponse`. Set `paymentMethod=ALL` for method selection. |
| `GET /api/payments/vnpay/return` | Public; VNPAY signature checked | Verifies Return parameters and issues a `303` redirect to the configured frontend result page. Does not update payment state. |
| `GET /api/payments/vnpay/ipn` | Public; VNPAY signature checked | Processes callback idempotently and returns `{"RspCode":"00","Message":"Confirm Success"}` when acknowledged. |
| `GET /api/payments/{paymentId}` | Owner or admin | Returns the authoritative `PaymentDTO` for status polling. |

## Sandbox verification checklist

- Deploy the backend after adding the VNPAY environment variables and Flyway migration V2.
- Check `https://YOUR-RENDER-SERVICE.onrender.com/health` reports `UP`.
- Send VNPAY the exact HTTPS IPN URL above and wait for confirmation it is registered.
- Ensure the test membership plan's currency is `VND`; do not guess a conversion from an old plan price.
- Start checkout in the UI and complete a VNPAY Sandbox payment/QR flow using VNPAY's official sandbox test details.
- Confirm the return page waits for IPN, the payment record changes to `SUCCESS`, and the plan activates (or fine becomes paid).
- Confirm cancellation/failure is not shown as success and does not activate membership.

The merchant signing secret was exposed in a chat message while preparing this integration. Rotate it in the VNPAY Sandbox merchant portal/support workflow and replace the Render `VNPAY_HASH_SECRET` value before sharing the project or using the integration further.
