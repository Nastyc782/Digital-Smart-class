# Digital Smart Class

E-learning platform: Spring Boot 3 (Java 17+) + MySQL 8. React frontend comes next.

## Folder contents
- `backend/digital-smart-class-api/` - Spring Boot API (not yet run or tested)
- `database/` - `schema.sql` and `seed.sql` (MySQL 8)
- `frontend-prototype/index.html` - clickable demo of every screen. Open it in a browser. Demo password for all accounts: `Demo@2026` (admin@demo.rw, teacher@demo.rw, student@demo.rw, accountant@demo.rw). It has no backend and resets on reload.
- `frontend/` - the real React app (to be built)

The prototype also shows features the backend does not have yet: quizzes, certificates, password reset by SMS, SMS alerts, teacher earnings and payouts, course chat rooms and announcements.

## Run the backend
1. Install JDK 17+, Maven 3.9+, MySQL 8.
2. Create the database: `mysql -u root -p < database/schema.sql && mysql -u root -p < database/seed.sql`
3. Set environment variables (or edit `application.properties`):
   `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (32+ characters), `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `ACCOUNTANT_EMAIL`, `ACCOUNTANT_PASSWORD`
4. `cd backend/digital-smart-class-api && mvn spring-boot:run` (API on http://localhost:8080)

The admin and accountant accounts are created on first start with BCrypt-hashed passwords. Change the default passwords before going live.

## Main endpoints
| Who | Endpoint | Purpose |
|---|---|---|
| public | `POST /api/auth/register-student`, `register-teacher`, `login` | Accounts and JWT |
| any user | `GET /api/courses`, `/api/courses/{id}`, `/api/notifications` | Catalog (no lesson content) |
| student | `POST /api/student/payments` | Submit MoMo message + reference (status PENDING) |
| student | `GET /api/student/courses/{id}/lessons` | 403 unless enrollment is active |
| student | `POST /api/student/lessons/{id}/progress` | Save watched seconds |
| all roles | `GET /api/files/{id}` | Protected download (enrollment or ownership checked) |
| teacher | `/api/teacher/courses`, `/lessons`, `/materials`, `/courses/{id}/students` | Own courses only, approved teachers only |
| admin, accountant | `/api/review/payments`, `/approve`, `/reject`, `/revenue` | Payment verification |
| admin | `/api/admin/teacher-applications`, `/users`, `/stats`, `/settings/completion-percent` | Platform control |

## Business rules enforced in code
- Payment approval is the only thing that creates an active enrollment (`PaymentService.approve`).
- Course price is read from the database, never from the request.
- Lessons and files are refused with 403 without an active enrollment (`AccessService`).
- Teachers can only touch and see students of their own courses; unapproved teachers cannot create anything.
- Video completion uses the admin-configurable percentage (default 90). Progress never goes backwards.
- Files are stored outside any public folder, with UUID names and an extension allow-list.
- Approvals, rejections, status changes and setting changes are written to `audit_logs`.

## Known limits and next steps
- Watched time is reported by the browser, so a determined user could fake it. A later version can add server-side heartbeats.
- Browsers cannot send an Authorization header from a `<video src>` tag. The React app should fetch protected media as a blob, or the API can issue short-lived signed URLs.
- Teacher CV and certificate upload is not built yet (the columns exist).
- Lessons carry a `module_title` field instead of a separate modules table.
- No automated tests yet. Phase 13 should add them, starting with the access rules.
- Next: React frontend (Vite), then MTN MoMo API integration for automatic payment confirmation.
