# UI and UX

## Internationalization

The interface supports English (`en`) and Vietnamese (`vi`). A language switch is available in public navigation and both portal layouts. The selected locale is stored in the non-sensitive `folio_locale` cookie for one year, rendered on the server to avoid hydration mismatch, and applied to the document `lang` attribute. Translation keys fall back to their English source text so backend-provided book, genre and plan names remain unchanged.

The design uses restrained indigo, slate and semantic success/warning/danger tokens in light and dark themes. Typography, eight-pixel radii, borders and spacing are consistent across cards, dialogs, forms and tables. Public pages emphasize discovery; member pages emphasize due dates and account status; admin pages emphasize scan-friendly operations.

All icon-only controls have labels. Forms connect visible labels and error text, dialogs use Radix focus management, keyboard focus is visible, a skip link targets main content and reduced-motion preferences are honored. `Ctrl/Cmd+K` opens a small command palette. Mutations disable repeated submissions and show one result toast. Tables use horizontal scrolling on mobile; navigation switches to a dialog.
