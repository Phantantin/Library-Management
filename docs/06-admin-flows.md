# Administration flows

The admin dashboard shows live counts and a loan-status chart from `/api/admin/statistics`. It does not invent revenue or time-series values absent from the database APIs.

Catalog staff can create/update/deactivate/permanently delete books, with explicit confirmation for irreversible deletion. Genre administration supports hierarchy, order and soft/hard deletion. Circulation supports checkout for a member, check-in and overdue recalculation. Reservation fulfillment, review moderation, fine creation/waiver and payment inspection use backend authority.

User administration is read-only because the domain has no account-status field and no safe role/status mutation service. Subscription administration includes plan CRUD, membership records and expired-membership deactivation. Payment records are read-only; staff cannot manually mark provider payments successful.
