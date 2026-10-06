"""Generate source-backed API inventory. Run from repository root."""
from pathlib import Path
import re

root = Path('src/main/java/com/zou')
out = Path('docs')
out.mkdir(exist_ok=True)
lines = ['# Frontend API contract', '', 'Source: Spring controllers, services, DTOs and mappers. Generated signatures below are authoritative; no fabricated response examples.', '',
         '## Transport and authorization', '',
         'JSON bodies; dates are ISO local dates/times (no timezone). BookReviewDTO uses yyyy-MM-dd HH:mm:ss. Bearer JWT lasts 24 hours. Roles: ROLE_USER, ROLE_ADMIN. No refresh endpoint. Raw DTOs, arrays, custom PageResponse and Spring Page coexist; do not assume a universal data envelope.', '',
         'Policy: public catalog/read-only genres/plans/reviews; authenticated personal actions; ADMIN catalog writes, cross-user reads/actions, fine administration and plan administration. Ownership is enforced in services.', '',
         'Errors: 400 validation/business rejection; 401 missing/expired credentials; 403 wrong role/owner; 404 missing resource; 409 integrity conflict; 500 unexpected failure. The frontend also handles network/timeout and non-JSON errors.', '']
resolved = {'createBooksBulk':'List<BookDTO>', 'checkoutBook':'BookLoanDTO','checkoutBookForUser':'BookLoanDTO','checkin':'BookLoanDTO','renew':'BookLoanDTO','getMyBookLoans':'PageResponse<BookLoanDTO>','searchAllBookLoans':'PageResponse<BookLoanDTO>','updateOverdueBookLoans':'ApiResponse','createReview':'BookReviewDTO','updateReview':'BookReviewDTO','deleteReview':'ApiResponse','createFine':'FineDTO','payFine':'PaymentInitiateResponse','waiveFine':'FineDTO','getMyFines':'List<FineDTO>','getAllFines':'PageResponse<FineDTO>','getAllGenre':'List<GenreDTO>','getGenreById':'GenreDTO','updateGenre':'GenreDTO','deleteGenre':'ApiResponse','hardDeleteGenre':'ApiResponse','getTopLevelGenre':'List<GenreDTO>','getTotalActiveGenres':'Long','getBookCountByGenres':'Long','vnpayReturn':'303 redirect to the configured frontend payment result page','vnpayIpn':'JSON object {"RspCode":"00","Message":"Confirm Success"} on acknowledged callbacks','getPayment':'PaymentDTO','getAllPayments':'Page<PaymentDTO>','createReservation':'ReservationDTO','createReservationForUser':'ReservationDTO','deleteReservation':'ReservationDTO','fulfillReservation':'ReservationDTO','subscribeToSubscription':'PaymentInitiateResponse','getUsersActiveSubscriptions':'SubscriptionDTO','getAllSubscriptions':'List<SubscriptionDTO>','deactivateExpiredSubscriptions':'ApiResponse','cancelSubscription':'SubscriptionDTO','activateSubscription':'SubscriptionDTO','getAllSubscriptionPlans':'List<SubscriptionPlanDTO>','createSubscriptionPlan':'SubscriptionPlanDTO','updateSubscriptionPlan':'SubscriptionPlanDTO','deleteSubscriptionPlan':'ApiResponse','addToWishlist':'WishlistDTO','getMyWishlist':'PageResponse<WishlistDTO>'}
count=0
for file in sorted((root/'controller').glob('*.java')):
    src = file.read_text(encoding='utf-8-sig')
    src = re.sub(r'/\*.*?\*/|//[^\n]*', '', src, flags=re.S)
    base = re.search(r'@RequestMapping\("([^"]*)"\)',src)
    base = base[1] if base else ''
    lines += ['## '+file.stem, '']
    for m in re.finditer(r'@(Get|Post|Put|Delete|Patch)Mapping(?:\(([^\n]*)\))?\s+public\s+ResponseEntity<(.+?)>\s+(\w+)\s*\((.*?)\)\s*(?:throws [^{]+)?\{',src,re.S):
        method,args,response,name,params=m.groups()
        path=re.search(r'"([^"]*)"',args or '')
        path=base+(path[1] if path else '') or '/'
        response=resolved.get(name,response)
        count+=1
        verb=method.upper()
        success_note = {'vnpayReturn':'HTTP 303 redirect', 'vnpayIpn':'HTTP 200 with the VNPAY RspCode acknowledgement'}.get(name, 'normally HTTP 200; some create actions return 201')
        is_public = path=='/' or path.startswith('/auth/') or path in ['/api/payments/vnpay/ipn','/api/payments/vnpay/return'] or (verb=='GET' and (path.startswith('/api/books') or path.startswith('/api/genres') or path=='/api/subscription-plans' or path.startswith('/api/reviews/book/'))) or (verb=='POST' and path=='/api/books/search')
        is_admin = path.startswith('/api/admin/') or path.startswith('/api/subscription-plans/admin/') or path.startswith('/api/subscriptions/admin') or path.startswith('/api/book-loans/admin/') or path in ['/api/users/list','/api/book-loans/search'] or path.startswith('/api/book-loans/checkout/user/') or path.startswith('/api/reservations/user/') or path.endswith('/fulfill') or (verb=='GET' and path in ['/api/reservations','/api/fines','/api/payments']) or (verb=='POST' and path in ['/api/fines','/api/fines/waive','/api/subscriptions/activate'])
        auth = 'Public; no JWT.' if is_public else ('Bearer JWT; ROLE_ADMIN.' if is_admin else 'Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.')
        lines += [f'### {verb} {path}', '', f'Source: `{file.name}::{name}`.', '', 'Request (exact Spring binding, including query defaults):', '', '```java', params.strip() or '// no parameters', '```', '', f'Response: `{response}` (schema below). Success: {success_note}.', '', f'Authentication / Role: {auth}', '', f'Frontend usage: {name}. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.', '']
lines += ['## JSON schemas from DTO declarations', '', 'Java Long/Integer/BigDecimal serialize as JSON numbers; Boolean/boolean as booleans; String and date/time as strings; List as arrays; nullable reference fields can be null. See mapper caveats in system overview. Password fields must become write-only before integration.', '']
for folder in ['payload/request','payload/response','payload/dto','domain']:
    for file in sorted((root/folder).glob('*.java')):
        src=file.read_text(encoding='utf-8-sig')
        lines += ['### '+file.stem, '', '```java']
        if folder=='domain': lines += [src[src.index('public enum'):].strip()]
        else:
            lines += [x.strip() for x in src.splitlines() if re.match(r'\s*(private|public)\s+(?!class|static)',x) or re.match(r'\s*@(Not|Min|Max|Size|Positive|Digits|Decimal|Json)',x)]
        lines += ['```','']
lines += ['## Supplemental response', '', '`BookStatsResponse`: `{ "totalActiveBooks": number, "totalAvailableBooks": number }` (counts titles, not copies).', '', '`GET /api/admin/statistics` returns live totals plus `loanStatuses`, 30-day `operationsTrend` (`date`, `loans`, `returns`, `newUsers`), provider-verified `revenueTrend`/`revenueTotals` separated by currency, and `popularBooks`/`popularGenres` count series.', '', '`Page<PaymentDTO>` uses Spring Page fields `content`, `number`, `size`, `totalElements`, `totalPages`, `first`, `last`, `empty`, `sort`, `pageable`, `numberOfElements`.', '', 'VNPAY checkout requests use VND and store the local payment ID as `vnp_TxnRef`. `paymentMethod=QR` selects `vnp_BankCode=VNPAYQR`; `ALL` lets the hosted VNPAY page offer its available methods. The Return endpoint validates the signed response and redirects for display only. Only the unauthenticated, signature-verified IPN endpoint changes payment/fine/subscription state. Configure its public HTTPS URL with the merchant: `/api/payments/vnpay/ipn`.', '', f'Endpoint count: {count}.']
(out/'frontend-api-contract.md').write_text('\n'.join(lines),encoding='utf-8')
(out/'03-api-contract.md').write_text('\n'.join(lines),encoding='utf-8')
print(f'Wrote {count} endpoints and all payload/enum schemas')
