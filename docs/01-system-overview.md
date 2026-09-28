# Repository analysis — baseline

Spring Boot 4.0.4, Java 17, Spring MVC, Security, JPA/Hibernate, MySQL, Lombok, JJWT, SMTP and Razorpay. The new Next.js frontend is in the workspace-level `frontend/` directory, alongside the backend. Backend Git root is Library-Management. Branch: feature/library-frontend.

## Inventory

16 controllers after integration work: AdminBook, AdminGenre, Auth, Book, BookLoan, BookReview, Fine, Genre, Home, Payment, Portal, Reservation, Subscription, SubscriptionPlan, User and Wishlist. The generated contract contains all 78 mapped endpoints and every request/response/DTO/enum declaration.

12 entities: Book, BookLoan, BookReview, Fine, Genre, PasswordResetToken, Payment, Reservation, Subscription, SubscriptionPlan, User, Wishlist. Repositories, mappers, service interfaces/implementations, payment publisher/listener and the single context test have been read.

## Business flows

- Signup always creates ROLE_USER; login compares BCrypt and issues signed JWT with email/authorities for 24 hours. No refresh, logout or account-status model. Password reset uses a single-use token expiring in five minutes and SMTP.
- Books: filter title/author/ISBN, genre, availability; pagination and sort; create/bulk/update/soft/permanent delete. ISBN is immutable on update. Genre supports parent/subgenre and soft/hard deletion.
- Checkout requires active subscription, active book/copies, no duplicate active loan, membership limit and no overdue loans. Two renewals maximum. Return increments copies unless lost. Returned loans permit reviews; one review per user/book.
- Reservation accepts unavailable books, rejects duplicate active reservation and active checkout; queue position, cancel and staff fulfillment. Intended limit five. Fulfillment must atomically checkout for the reserved member.
- Wishlist is per-user, unique by book; paginated list, add, remove.
- Fines are staff-created against a loan: overdue/damage/loss/processing; pay or waive. Existing model has no partial-payment accounting despite PARTIALLY_PAID enum and unused DTO fields.
- Subscription snapshots plan price and borrowing limits, starts inactive, creates Razorpay payment link. Verified captured payment activates membership through event listener. Cancel and expiry deactivate. Plans support create/update/delete.
- Payment uses hosted Razorpay links and backend fetch of provider payment. STRIPE enum exists but no implementation. Local code uses VND while plan default is INR: currency handling requires a controlled fix and gateway validation.

## Baseline defects and resolution

These findings were recorded before frontend implementation. Items 1-10 were corrected by the integration work except for the explicit domain limitations in the implementation report.

1. Most catalog mutations and global member data are accessible to any authenticated user. Several actions lack ownership checks. Public discovery is blocked by /api/** authentication.
2. JWT secret and bootstrap admin password are hard-coded. Profile returns User entity including password hash. Reset URL is empty; weak request validation. JWT filter mishandles malformed headers.
3. Checkout loads BookLoan by book ID, not Book. Builder omits non-null isOverdue. Personal loan sort uses createAt rather than createdAt. Mapper remainingDays uses checkoutDate rather than dueDate.
4. Book mapper sets genreId to book ID, omits cover URL; update writes caller timestamps and ignores price. Search defaults reversed, nullable availability unboxed. activeOnly ignored. Available-copy validation return value ignored.
5. Review deletion saves without deleting. Missing personal/admin review lists and aggregate rating.
6. Reservation on-behalf action uses current staff user; limit is off by one; fulfillment changes state before checkout without transaction. Page flags incomplete.
7. Genre book count always zero; top-level query incorrect; repository declares invalid extra-argument derived query.
8. Payment does not persist fine association; fine verification unimplemented, listener does nothing for fines; activation accepts any paymentId without verification; replay and ownership unguarded. Subscription request ID can overwrite existing subscription; requested user ID ignored when staff checkout checks membership.
9. Missing personal payment history, subscription history and profile update APIs. User role/status update and deletion are not existing functionality; no account-status field. Analytics APIs absent.
10. GlobalException maps Exception but accepts GenreException; UserException extends Throwable. Maven Lombok scope is invalid. Single test depends on local MySQL/secrets.

## Analysis boundary

The inventory and defects above describe the source baseline used to design the integration. The implemented result and remaining environment-dependent checks are recorded in `IMPLEMENTATION-REPORT.md`. External SMTP/Razorpay availability, production MySQL contents and deployed browser flows require the target environment and valid sandbox credentials.
