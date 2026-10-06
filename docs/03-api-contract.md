# Frontend API contract

Source: Spring controllers, services, DTOs and mappers. Generated signatures below are authoritative; no fabricated response examples.

## Transport and authorization

JSON bodies; dates are ISO local dates/times (no timezone). BookReviewDTO uses yyyy-MM-dd HH:mm:ss. Bearer JWT lasts 24 hours. Roles: ROLE_USER, ROLE_ADMIN. No refresh endpoint. Raw DTOs, arrays, custom PageResponse and Spring Page coexist; do not assume a universal data envelope.

Policy: public catalog/read-only genres/plans/reviews; authenticated personal actions; ADMIN catalog writes, cross-user reads/actions, fine administration and plan administration. Ownership is enforced in services.

Errors: 400 validation/business rejection; 401 missing/expired credentials; 403 wrong role/owner; 404 missing resource; 409 integrity conflict; 500 unexpected failure. The frontend also handles network/timeout and non-JSON errors.

## AdminBookController

### GET /api/admin/books/{id}

Source: `AdminBookController.java::getBook`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id
```

Response: `BookDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: getBook. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/admin/books/search

Source: `AdminBookController.java::search`.

Request (exact Spring binding, including query defaults):

```java
@RequestBody BookSearchRequest request
```

Response: `PageResponse<BookDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: search. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/admin/books

Source: `AdminBookController.java::createBook`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody BookDTO bookDTO
```

Response: `BookDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: createBook. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## AdminGenreController

### GET /api/admin/genres

Source: `AdminGenreController.java::list`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `List<GenreDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: list. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## AuthController

### POST /auth/signup

Source: `AuthController.java::signupHandler`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody UserDTO req
```

Response: `AuthResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: signupHandler. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /auth/login

Source: `AuthController.java::loginHandler`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody LoginRequest req
```

Response: `AuthResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: loginHandler. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /auth/forgot-password

Source: `AuthController.java::forgotPassword`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody ForgotPasswordRequest request
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: forgotPassword. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /auth/reset-password

Source: `AuthController.java::resetPassword`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody ResetPasswordRequest request
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: resetPassword. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## BookController

### POST /api/books

Source: `BookController.java::createBook`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody BookDTO bookDTO
```

Response: `BookDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: createBook. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/books/bulk

Source: `BookController.java::createBooksBulk`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody List<BookDTO> bookDTOs
```

Response: `List<BookDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: createBooksBulk. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/books/{id}

Source: `BookController.java::getBookById`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id
```

Response: `BookDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getBookById. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### PUT /api/books/{id}

Source: `BookController.java::updateBook`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO
```

Response: `BookDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: updateBook. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### DELETE /api/books/{id}

Source: `BookController.java::deleteBook`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: deleteBook. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### DELETE /api/books/{id}/permanent

Source: `BookController.java::deletePermanentBook`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: deletePermanentBook. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/books

Source: `BookController.java::getBooks`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(required = false) Long genreId,
            @RequestParam(required = false, defaultValue = "false") Boolean availableOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
```

Response: `PageResponse<BookDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getBooks. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/books/search

Source: `BookController.java::advancedSearch`.

Request (exact Spring binding, including query defaults):

```java
@RequestBody BookSearchRequest bookSearchRequest
```

Response: `PageResponse<BookDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: advancedSearch. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/books/stats

Source: `BookController.java::getBookStats`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `BookStatsResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getBookStats. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/books/featured

Source: `BookController.java::featured`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue = "6") int limit
```

Response: `List<BookDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: featured. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/books/popular

Source: `BookController.java::popular`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue = "6") int limit
```

Response: `List<BookDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: popular. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## BookLoanController

### POST /api/book-loans/checkout

Source: `BookLoanController.java::checkoutBook`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody CheckoutRequest checkoutRequest
```

Response: `BookLoanDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: checkoutBook. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/book-loans/checkout/user/{userId}

Source: `BookLoanController.java::checkoutBookForUser`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest
```

Response: `BookLoanDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: checkoutBookForUser. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/book-loans/checkin

Source: `BookLoanController.java::checkin`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody CheckinRequest checkinRequest
```

Response: `BookLoanDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: checkin. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/book-loans/renew

Source: `BookLoanController.java::renew`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody RenewalRequest  renewalRequest
```

Response: `BookLoanDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: renew. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/book-loans/my

Source: `BookLoanController.java::getMyBookLoans`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(required = false)BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
```

Response: `PageResponse<BookLoanDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: getMyBookLoans. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/book-loans/search

Source: `BookLoanController.java::searchAllBookLoans`.

Request (exact Spring binding, including query defaults):

```java
@RequestBody BookLoanSearchRequest bookLoanSearchRequest
```

Response: `PageResponse<BookLoanDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: searchAllBookLoans. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/book-loans/admin/update-overdue

Source: `BookLoanController.java::updateOverdueBookLoans`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: updateOverdueBookLoans. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## BookReviewController

### POST /api/reviews

Source: `BookReviewController.java::createReview`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody CreateReviewRequest request
```

Response: `BookReviewDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: createReview. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### PUT /api/reviews/{id}

Source: `BookReviewController.java::updateReview`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request
```

Response: `BookReviewDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: updateReview. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### DELETE /api/reviews/{reviewId}

Source: `BookReviewController.java::deleteReview`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long reviewId
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: deleteReview. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/reviews/book/{bookId}

Source: `BookReviewController.java::getReviewsByBook`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
```

Response: `PageResponse<BookReviewDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getReviewsByBook. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## FineController

### POST /api/fines

Source: `FineController.java::createFine`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody CreateFineRequest fineRequest
```

Response: `FineDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: createFine. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/fines/{id}/pay

Source: `FineController.java::payFine`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id,
            @RequestParam(required = false) String paymentMethod,
            HttpServletRequest request
```

Response: `PaymentInitiateResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: payFine. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/fines/waive

Source: `FineController.java::waiveFine`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody WaiveFineRequest waiveFineRequest
```

Response: `FineDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: waiveFine. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/fines/my

Source: `FineController.java::getMyFines`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(required = false) FineStatus status,
            @RequestParam(required = false) FineType type
```

Response: `List<FineDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: getMyFines. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/fines

Source: `FineController.java::getAllFines`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(required = false) FineStatus status,
            @RequestParam(required = false) FineType type,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
```

Response: `PageResponse<FineDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: getAllFines. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## GenreController

### POST /api/genres/create

Source: `GenreController.java::addGenre`.

Request (exact Spring binding, including query defaults):

```java
@RequestBody GenreDTO genreSTO
```

Response: `GenreDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: addGenre. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/genres

Source: `GenreController.java::getAllGenre`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `List<GenreDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getAllGenre. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/genres/{genreId}

Source: `GenreController.java::getGenreById`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable("genreId") Long genreId
```

Response: `GenreDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getGenreById. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### PUT /api/genres/{genreId}

Source: `GenreController.java::updateGenre`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable("genreId") Long genreId,
            @RequestBody GenreDTO genre
```

Response: `GenreDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: updateGenre. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### DELETE /api/genres/{genreId}

Source: `GenreController.java::deleteGenre`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable("genreId") Long genreId
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: deleteGenre. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### DELETE /api/genres/{genreId}/hard

Source: `GenreController.java::hardDeleteGenre`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable("genreId") Long genreId
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: hardDeleteGenre. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/genres/top-level

Source: `GenreController.java::getTopLevelGenre`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `List<GenreDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getTopLevelGenre. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/genres/count

Source: `GenreController.java::getTotalActiveGenres`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `Long` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getTotalActiveGenres. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/genres/popular

Source: `GenreController.java::popular`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue = "6") int limit
```

Response: `List<GenreDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: popular. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/genres/{id}/book-count

Source: `GenreController.java::getBookCountByGenres`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id
```

Response: `Long` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getBookCountByGenres. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## HomeController

### GET /

Source: `HomeController.java::home`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `String` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: home. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /health

Source: `HomeController.java::health`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `Map<String, Object>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: health. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## PaymentController

### GET /api/payments/vnpay/return

Source: `PaymentController.java::vnpayReturn`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam Map<String, String> params
```

Response: `303 redirect to the configured frontend payment result page` (schema below). Success: HTTP 303 redirect.

Authentication / Role: Public; no JWT.

Frontend usage: vnpayReturn. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/payments/vnpay/ipn

Source: `PaymentController.java::vnpayIpn`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam Map<String, String> params
```

Response: `JSON object {"RspCode":"00","Message":"Confirm Success"} on acknowledged callbacks` (schema below). Success: HTTP 200 with the VNPAY RspCode acknowledgement.

Authentication / Role: Public; no JWT.

Frontend usage: vnpayIpn. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/payments/{paymentId}

Source: `PaymentController.java::getPayment`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long paymentId
```

Response: `PaymentDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: getPayment. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/payments

Source: `PaymentController.java::getAllPayments`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
```

Response: `Page<PaymentDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: getAllPayments. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## PortalController

### GET /api/reviews/my

Source: `PortalController.java::myReviews`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size
```

Response: `Page<BookReviewDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: myReviews. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/admin/reviews

Source: `PortalController.java::reviews`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size
```

Response: `Page<BookReviewDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: reviews. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/payments/my

Source: `PortalController.java::payments`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size
```

Response: `Page<PaymentDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: payments. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/subscriptions/my

Source: `PortalController.java::subscriptions`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size
```

Response: `Page<SubscriptionDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: subscriptions. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/admin/users

Source: `PortalController.java::users`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue="") String search,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size
```

Response: `Page<UserDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: users. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/admin/users/{id}

Source: `PortalController.java::user`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id
```

Response: `UserDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: user. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### PUT /api/users/profile

Source: `PortalController.java::profile`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody ProfileRequest request
```

Response: `UserDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: profile. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/admin/statistics

Source: `PortalController.java::statistics`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `Map<String,Object>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: statistics. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## ReservationController

### POST /api/reservations

Source: `ReservationController.java::createReservation`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody ReservationRequest reservationRequest
```

Response: `ReservationDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: createReservation. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/reservations/user/{userId}

Source: `ReservationController.java::createReservationForUser`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long userId,
            @Valid @RequestBody ReservationRequest reservationRequest
```

Response: `ReservationDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: createReservationForUser. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### DELETE /api/reservations/{id}

Source: `ReservationController.java::deleteReservation`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id
```

Response: `ReservationDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: deleteReservation. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/reservations/{id}/fulfill

Source: `ReservationController.java::fulfillReservation`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long id
```

Response: `ReservationDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: fulfillReservation. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/reservations/my

Source: `ReservationController.java::getMyReservations`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
```

Response: `PageResponse<ReservationDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: getMyReservations. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/reservations

Source: `ReservationController.java::searchReservations`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
```

Response: `PageResponse<ReservationDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: searchReservations. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## SubscriptionController

### POST /api/subscriptions/subscribe

Source: `SubscriptionController.java::subscribeToSubscription`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody SubscriptionPurchaseRequest purchase,
            HttpServletRequest request
```

Response: `PaymentInitiateResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: subscribeToSubscription. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/subscriptions/user/active

Source: `SubscriptionController.java::getUsersActiveSubscriptions`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(required = false) Long userId
```

Response: `SubscriptionDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: getUsersActiveSubscriptions. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/subscriptions/admin

Source: `SubscriptionController.java::getAllSubscriptions`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size
```

Response: `List<SubscriptionDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: getAllSubscriptions. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/subscriptions/admin/deactivate-expired

Source: `SubscriptionController.java::deactivateExpiredSubscriptions`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: deactivateExpiredSubscriptions. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/subscriptions/admin/deactivate-expired

Source: `SubscriptionController.java::deactivateExpiredSubscriptionsLegacy`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `?` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: deactivateExpiredSubscriptionsLegacy. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/subscriptions/cancel/{subscriptionId}

Source: `SubscriptionController.java::cancelSubscription`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long subscriptionId,
            @RequestParam(required = false) String reason
```

Response: `SubscriptionDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: cancelSubscription. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/subscriptions/activate

Source: `SubscriptionController.java::activateSubscription`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam Long subscriptionId,
            @RequestParam Long paymentId
```

Response: `SubscriptionDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: activateSubscription. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## SubscriptionPlanController

### GET /api/subscription-plans

Source: `SubscriptionPlanController.java::getAllSubscriptionPlans`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `List<SubscriptionPlanDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Public; no JWT.

Frontend usage: getAllSubscriptionPlans. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### POST /api/subscription-plans/admin/create

Source: `SubscriptionPlanController.java::createSubscriptionPlan`.

Request (exact Spring binding, including query defaults):

```java
@Valid @RequestBody SubscriptionPlanDTO subscriptionPlanDTO
```

Response: `SubscriptionPlanDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: createSubscriptionPlan. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### PUT /api/subscription-plans/admin/{id}

Source: `SubscriptionPlanController.java::updateSubscriptionPlan`.

Request (exact Spring binding, including query defaults):

```java
@RequestBody SubscriptionPlanDTO subscriptionPlanDTO,
            @PathVariable long id
```

Response: `SubscriptionPlanDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: updateSubscriptionPlan. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### DELETE /api/subscription-plans/admin/{id}

Source: `SubscriptionPlanController.java::deleteSubscriptionPlan`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable long id
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: deleteSubscriptionPlan. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## UserController

### GET /api/users/list

Source: `UserController.java::getAllUsers`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `List<UserDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; ROLE_ADMIN.

Frontend usage: getAllUsers. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/users/profile

Source: `UserController.java::getUserProfile`.

Request (exact Spring binding, including query defaults):

```java
// no parameters
```

Response: `UserDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: getUserProfile. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## WishlistController

### POST /api/wishlist/add/{bookId}

Source: `WishlistController.java::addToWishlist`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long bookId,
            @RequestParam(required = false) String notes
```

Response: `WishlistDTO` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: addToWishlist. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### DELETE /api/wishlist/remove/{bookId}

Source: `WishlistController.java::removeFromWishlist`.

Request (exact Spring binding, including query defaults):

```java
@PathVariable Long bookId
```

Response: `ApiResponse` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: removeFromWishlist. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

### GET /api/wishlist/my-wishlist

Source: `WishlistController.java::getMyWishlist`.

Request (exact Spring binding, including query defaults):

```java
@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
```

Response: `PageResponse<WishlistDTO>` (schema below). Success: normally HTTP 200; some create actions return 201.

Authentication / Role: Bearer JWT; authenticated ROLE_USER or ROLE_ADMIN. Owner checks apply to personal records.

Frontend usage: getMyWishlist. Error cases: validation, resource existence, role/ownership and domain preconditions described in the system overview.

## JSON schemas from DTO declarations

Java Long/Integer/BigDecimal serialize as JSON numbers; Boolean/boolean as booleans; String and date/time as strings; List as arrays; nullable reference fields can be null. See mapper caveats in system overview. Password fields must become write-only before integration.

### BookLoanSearchRequest

```java
private Long userId;
private Long bookId;
private BookLoanStatus status;
private Boolean overdueOnly;
private Boolean unpaidFinesOnly;
private LocalDate startDate;
private LocalDate endDate;
```

### BookSearchRequest

```java
private String searchTerm;
private Long genreId;
private Boolean availableOnly=false;
private Boolean activeOnly=true;
private Integer pageSize=20;
private Integer page=0;
private String sortBy="createdAt";
private String sortDirection="DESC";
```

### CheckinRequest

```java
@NotNull(message = "Book loan ID mandatory")
private Long bookLoanId;
private BookLoanStatus condition = BookLoanStatus.RETURNED;
private String notes;
```

### CheckoutRequest

```java
@NotNull(message = "Book ID is mandatory")
private Long bookId;
@Min(value = 1, message = "Checkout days must be at least 1")
private Integer checkoutDays=14;
private String notes;
```

### CreateFineRequest

```java
@NotNull(message = "Book loan ID is mandatory")
private Long bookLoanId;
@NotNull(message = "Fine type is mandatory")
private FineType type;
@NotNull(message = "Fine amount is mandatory")
@Positive(message = "Fine amount must be positive")
private Long amount;
private String reason;
private String notes;
```

### CreateReviewRequest

```java
@NotNull(message = "Book ID is mandatory")
private Long bookId;
@NotNull(message = "Rating is mandatory")
@Min(value = 1, message = "Rating must be at least 1")
@Max(value = 5, message = "Rating must not exceed 5")
private Integer rating;
@NotBlank(message = "Review text is mandatory")
@Size(
private String reviewText;
@Size(
private String title;
```

### ForgotPasswordRequest

```java
private String email;
```

### LoginRequest

```java
@NotNull(message = "user name or email is required")
private String email;
@NotNull(message = "password is required")
private String password;
```

### PaymentInitiateRequest

```java
@NotNull(message = "User ID is mandatory")
private Long userId;
private Long bookLoanId;
@NotNull(message = "Payment type is mandatory")
private PaymentType paymentType;
@NotNull(message = "Payment gateway is mandatory")
private PaymentGateway gateway;
@NotNull(message = "Amount is mandatory")
@Positive(message = "Amount must be positive")
private Long amount;
@Size(max = 500, message = "Description must not exceed 500 characters")
private String description;
private Long fineId;
private Long subscriptionId;
@Size(max = 500, message = "Success URL must not exceed 500 characters")
private String successUrl;
@Size(max = 500, message = "Cancel URL must not exceed 500 characters")
private String cancelUrl;
private String paymentMethod;
private String ipAddress;
```

### ProfileRequest

```java
public record ProfileRequest(@NotBlank @Size(max=100) String fullName, @Size(max=30) String phone) {}
```

### RenewalRequest

```java
@NotNull(message = "Book loan ID is mandatory")
private Long bookLoanId;
@Min(value = 1, message = "Extension days must be at least 1")
private Integer extensionDays;
private String notes;
```

### ReservationRequest

```java
@NotNull(message = "Book Id is mandatory")
private Long bookId;
private String notes;
```

### ReservationSearchRequest

```java
private Long userId;
private Long bookId;
private ReservationStatus status;
private Boolean activeOnly;
```

### ResetPasswordRequest

```java
private String token;
private String password;
```

### SubscriptionPurchaseRequest

```java
@NotNull
@Positive
private Long planId;
private String paymentMethod;
```

### UpdateReviewRequest

```java
@NotNull(message = "Rating is mandatory")
@Min(value = 1, message = "Rating must be at least 1")
@Max(value = 5, message = "Rating must not exceed 5")
private Integer rating;
@NotBlank(message = "Review text is mandatory")
@Size(
private String reviewText;
@Size(
private String title;
```

### WaiveFineRequest

```java
@NotNull(message = "Fine Id is mandatory")
private Long fineId;
@NotBlank(message = "Waiver reason is mandatory")
private String reason;
```

### ApiResponse

```java
private String message;
private Boolean status;
```

### AuthResponse

```java
private String jwt;
private String message;
private String title;
private UserDTO user;
```

### PageResponse

```java
private List<T> content;
private int pageNumber;
private int pageSize;
private long totalElements;
private int totalPages;
private boolean last;
private boolean first;
private boolean empty;
```

### PaymentInitiateResponse

```java
private Long paymentId;
private PaymentGateway gateway;
private String transactionId;
private String gatewayOrderId;
private Long amount;
private String description;
private String checkoutUrl;
private String message;
private Boolean success;
```

### BookDTO

```java
private Long id;
@NotBlank(message = "ISBN is mandatory")
private String isbn;
@NotBlank(message = "Title is mandatory")
@Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
private String title;
@NotBlank(message = "Author is mandatory")
@Size(min = 1, max = 255, message = "Author name must be between 1 and 255 characters")
private String author;
@NotNull(message = "Genre is mandatory")
private Long genreId;
private String genreName;
private String genreCode;
@Size(max = 100, message = "Publisher name must not exceed 100 characters")
private String publisher;
private LocalDate publicationDate;
@Size(max = 20, message = "Language must not exceed 20 characters")
private String language;
@Min(value = 1, message = "Pages must be at least 1")
@Max(value = 50000, message = "Pages must not exceed 50000")
private Integer pages;
@Size(max = 2000, message = "Description must not exceed 2000 characters")
private String description;
@Min(value = 0, message = "Total copies cannot be negative")
@NotNull(message = "Total copies is mandatory")
private Integer totalCopies;
@Min(value = 0, message = "Available copies cannot be negative")
@NotNull(message = "Available copies is mandatory")
private Integer availableCopies;
@DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
@Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimals")
private BigDecimal price;
@Size(max = 500, message = "Image URL must not exceed 500 characters")
private String coverImageUrl;
private Boolean alreadyHaveLoan;
private Boolean alreadyHaveReservation;
private Boolean active;
private Boolean featured;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

### BookLoanDTO

```java
private Long id;
private Long userId;
private String userName;
private String userEmail;
private Long bookId;
private String bookTitle;
private String bookIsbn;
private String bookAuthor;
private String bookCoverImage;
private BookLoanType bookLoanType;
private BookLoanStatus bookLoanStatus;
private LocalDate checkoutDate;
private LocalDate dueDate;
private Long remainingDays;
private LocalDate returnDate;
private Integer renewalCount;
private Integer maxRenewals;
private BigDecimal finePaid;
private String notes;
private Boolean isOverdue;
private Integer overdueDays;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

### BookReviewDTO

```java
private Long id;
@NotNull(message = "User ID is mandatory")
private Long userId;
private String userName;
@NotNull(message = "Book ID is mandatory")
private Long bookId;
private String bookTitle;
@NotNull(message = "Rating is mandatory")
@Min(value = 1, message = "Rating must be at least 1")
@Max(value = 5, message = "Rating must not exceed 5")
private Integer rating;
@NotBlank(message = "Review text is mandatory")
@Size(
private String reviewText;
@Size(
private String title;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime createdAt;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime updatedAt;
```

### FineDTO

```java
private Long id;
@NotNull(message = "Book loan ID is mandatory")
private Long bookLoanId;
private String bookTitle;
private String bookIsbn;
@NotNull(message = "User ID is mandatory")
private Long userId;
private String userName;
private String userEmail;
@NotNull(message = "Fine type is mandatory")
private FineType type;
@NotNull(message = "Fine amount is mandatory")
@PositiveOrZero(message = "Fine amount cannot be negative")
private Long amount;
@PositiveOrZero(message = "Amount paid cannot be negative")
private Long amountPaid;
private Long amountOutstanding;
@NotNull(message = "Fine status is mandatory")
private FineStatus status;
private String reason;
private String notes;
private Long waivedByUserId;
private String waivedByUserName;
private LocalDateTime waivedAt;
private String waiverReason;
private LocalDateTime paidAt;
private Long processedByUserId;
private String processedByUserName;
private String transactionId;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

### GenreDTO

```java
private Long id;
private String code;
private String name;
private String description;
private Integer displayOrder = 0;
private Boolean active;
private Long parentGenreId;
private String parentGenreName;
private List<GenreDTO> subGenre;
private Long bookCount;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

### PaymentDTO

```java
private Long id;
@NotNull(message = "User ID is mandatory")
private Long userId;
private String userName;
private String userEmail;
private Long bookLoanId;
private Long subscriptionId;
@NotNull(message = "Payment type is mandatory")
private PaymentType paymentType;
private PaymentStatus status;
@NotNull(message = "Payment gateway is mandatory")
private PaymentGateway gateway;
@NotNull(message = "Amount is mandatory")
@Positive(message = "Amount must be positive")
private Long amount;
private String currency;
private Long fineId;
private String transactionId;
private String gatewayPaymentId;
private String gatewayOrderId;
private String gatewaySignature;
private String description;
private String failureReason;
private Integer retryCount;
private LocalDateTime initiatedAt;
private LocalDateTime completedAt;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

### ReservationDTO

```java
private Long id;
private Long userId;
private String userName;
private String userEmail;
private Long bookId;
private String bookTitle;
private String bookIsbn;
private String bookAuthor;
private Boolean isBookAvailable;
private ReservationStatus status;
private LocalDateTime reservedAt;
private LocalDateTime availableAt;
private LocalDateTime availableUntil;
private LocalDateTime fulfilledAt;
private LocalDateTime cancelledAt;
private Integer queuePosition;
private Boolean notificationSent;
private String notes;
private String content;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
private boolean isExpired;
private boolean canBeCancelled;
private Long hoursUntilExpiry; // Hours remaining for pickup
```

### SubscriptionDTO

```java
private Long id;
@NotNull(message = "User ID is mandatory")
private Long userId;
private String userName;
private String userEmail;
@NotNull(message = "Subscription plan ID is mandatory")
private Long planId;
private String planName;
private String planCode;
private Long price;
private String currency;
private LocalDate startDate;
private LocalDate endDate;
private Integer maxBooksAllowed;
private Integer maxDaysPerBook;
private Boolean autoRenew;
private LocalDateTime cancelledAt;
private String cancellationReason;
private String notes;
private Long daysRemaining;
private Boolean isActive;
private Boolean isValid;
private Boolean isExpired;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

### SubscriptionPlanDTO

```java
private Long id;
@NotBlank(message = "Plan code is mandatory")
private String planCode;
@NotBlank(message = "Plan name is mandatory")
private String name;
private String description;
@NotNull(message = "Duration is mandatory")
@Positive(message = "Duration must be positive")
private Integer durationDays;
@NotNull(message = "Price is mandatory")
@Positive(message = "Price must be positive")
private Long price;
private String currency;
@NotNull(message = "Max books allowed is mandatory")
@Positive(message = "Max books must be positive")
private Integer maxBooksAllowed;
@NotNull(message = "Max days per book is mandatory")
@Positive(message = "Max days must be positive")
private Integer maxDaysPerBook;
private Integer displayOrder;
private Boolean isActive;
private Boolean isFeatured;
private String badgeText;
private String adminNotes;
private Double priceInMajorUnits;
private Double monthlyEquivalentPrice;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
private String createdBy;
private String updatedBy;
```

### UserDTO

```java
private Long id;
private String email;
@JsonProperty("fullName")
private String fullName;
private String userName;
private UserRole role;
private String phone;
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String password;
private LocalDateTime lastLogin;
```

### WishlistDTO

```java
private Long id;
private Long userId;
private String userFullName;
private BookDTO book;
private LocalDateTime addedAt;
private String notes;
```

### AuthProvider

```java
public enum AuthProvider {

    LOCAL,
    GOOGLE
}
```

### BookLoanStatus

```java
public enum BookLoanStatus {

    CHECKED_OUT,
    RETURNED,
    OVERDUE,
    LOST,
    DAMAGED
}
```

### BookLoanType

```java
public enum BookLoanType {

    CHECKOUT,

    RENEWAL,

    RETURN
}
```

### FineStatus

```java
public enum FineStatus {

    PENDING,
    PARTIALLY_PAID,
    PAID,
    WAIVED
}
```

### FineType

```java
public enum FineType {

    OVERDUE,
    DAMAGE,
    LOSS,
    PROCESSING
}
```

### PaymentGateway

```java
public enum PaymentGateway {
    RAZORPAY,
    STRIPE,
    VNPAY
}
```

### PaymentStatus

```java
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED,
    PROCESSING,

}
```

### PaymentType

```java
public enum PaymentType {
    FINE, MEMBERSHIP,
    LOST_BOOK_PENALTY,

    DAMAGED_BOOK_PENALTY,

    REFUND,
}
```

### ReservationStatus

```java
public enum ReservationStatus {

    PENDING,
    AVAILABLE,
    FULFILLED,
    CANCELLED,
    EXPIRED
}
```

### UserRole

```java
public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN
}
```

## Supplemental response

`BookStatsResponse`: `{ "totalActiveBooks": number, "totalAvailableBooks": number }` (counts titles, not copies).

`GET /api/admin/statistics` returns live totals plus `loanStatuses`, 30-day `operationsTrend` (`date`, `loans`, `returns`, `newUsers`), provider-verified `revenueTrend`/`revenueTotals` separated by currency, and `popularBooks`/`popularGenres` count series.

`Page<PaymentDTO>` uses Spring Page fields `content`, `number`, `size`, `totalElements`, `totalPages`, `first`, `last`, `empty`, `sort`, `pageable`, `numberOfElements`.

VNPAY checkout requests use VND and store the local payment ID as `vnp_TxnRef`. `paymentMethod=QR` selects `vnp_BankCode=VNPAYQR`; `ALL` lets the hosted VNPAY page offer its available methods. The Return endpoint validates the signed response and redirects for display only. Only the unauthenticated, signature-verified IPN endpoint changes payment/fine/subscription state. Configure its public HTTPS URL with the merchant: `/api/payments/vnpay/ipn`.

Endpoint count: 81.