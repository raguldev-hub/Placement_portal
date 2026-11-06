# College Placement Portal

Full-stack placement management system: Spring Boot + MySQL backend, vanilla HTML/CSS/JS frontend.

## Backend setup

1. **Create the database** (or let it auto-create — `application.yml` uses `createDatabaseIfNotExist=true`):
   ```sql
   CREATE DATABASE placement_portal;
   ```

2. **Configure credentials** in `backend/src/main/resources/application.yml`:
   ```yaml
   spring.datasource.username: root
   spring.datasource.password: root
   ```

3. **Run the app**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   Server starts on `http://localhost:8080`. Hibernate creates all tables automatically (`ddl-auto: update`).

4. **Swagger UI**: `http://localhost:8080/swagger-ui.html`

5. **Default admin account** (seeded automatically on first run):
   - Email: `admin@placement.college`
   - Password: `Admin@123`

## Frontend setup

The frontend is static HTML/CSS/JS — no build step. Just serve the `frontend/` folder:

```bash
cd frontend
python3 -m http.server 5500
```

Then open `http://localhost:5500`. It will redirect to the login page.

> If you serve the frontend from a different origin than `localhost:8080`, CORS is already open (`*`) in `SecurityConfig`, so this works out of the box. If your backend runs on a different host/port, update `API_BASE` in `frontend/js/api.js`.

## Quick test flow

1. Register a **Company** account → post a placement drive (set min CGPA, max backlogs, allowed branches).
2. Register a **Student** account → go to Drives → see the drive with an automatic eligibility badge.
3. Apply — the backend re-validates eligibility server-side (`EligibilityService`) before accepting the application.
4. Log in as the **Company** → Applicants → move the application through `APPLIED → UNDER_REVIEW → SHORTLISTED → INTERVIEW → SELECTED/REJECTED`.
5. Log in as **Admin** (`admin@placement.college`) → Dashboard shows aggregated stats; Applications page lets the placement office override any status.

## Postman

Import the Swagger JSON (`http://localhost:8080/v3/api-docs`) directly into Postman via "Import → Link" for a ready-made collection of all 34 endpoints.

## Project structure

```
placement-portal/
├── backend/     Spring Boot API (Maven)
└── frontend/    Static HTML/CSS/JS client
```

See the architecture, ER diagram, and full endpoint list in the accompanying build notes.
