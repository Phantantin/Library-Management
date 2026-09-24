# Member flows

1. A visitor searches/filter books, views a book and public reviews, or browses active genres.
2. Signup or login establishes an HttpOnly session. Member pages are server-protected.
3. Borrow validates active membership, copy inventory, duplicate loans, borrowing limit and overdue loans. Renew validates ownership, renewal limit, due date and plan duration. Return updates the copy under a database lock.
4. Unavailable books can be reserved; the backend assigns queue position. Members can cancel active reservations. Staff fulfillment creates the loan in one transaction.
5. Wishlist add/remove and review create/edit/delete invalidate their query caches. A review requires a returned loan and only one review per user/book.
6. Fine payment or membership subscribe creates a hosted Razorpay link. The callback sends the provider payment ID to Spring, which fetches provider records, validates status/amount/currency/link ownership and only then updates fine/subscription state.
7. Profile updates name and phone. Password changes use the reset flow because no authenticated password-change endpoint exists.
