# Deploy GitHub Repository Searcher to Render

This project is a **Spring Boot 3.3.5 (Java 21)** backend that:
- Searches GitHub repositories via the GitHub REST API (`POST /api/github/search`)
- Persists results to PostgreSQL with upsert semantics
- Retrieves stored repos with filters (`GET /api/github/repositories`)
- Exposes interactive API docs via Swagger UI (`/swagger-ui.html`)

## ⚠️ Important before you start

Render **no longer offers a free tier** for web services or managed PostgreSQL. You will need a **Starter plan** (~$7/month each) for both the web service and the database, or you can pause/delete them after the interview.

**Live URL you will hand to the interviewer:**
```
https://githubrepositorysearcher.onrender.com
```
(Swagger UI: `https://githubrepositorysearcher.onrender.com/swagger-ui.html`)

---

## Step 1: Code is already on GitHub

The project is already pushed to:
```
https://github.com/Dineshrd0503/GithubRepositorySearcher
```
Render deploys directly from this repository — no further code push is needed.

---

## Step 2: Create the Render services

### Option A: Render Blueprint (recommended — creates DB + web service together)

1. Go to https://dashboard.render.com and log in.
2. Go to **Blueprints** → **New Blueprint Instance**.
3. Connect your GitHub account and select the `Dineshrd0503/GithubRepositorySearcher` repo.
4. Render will detect `render.yaml` and show two services to create:
   - `githubrepositorysearcher-db` (PostgreSQL, Starter)
   - `githubrepositorysearcher` (Web service, Docker)
5. Click **Apply / Create**.

### Option B: Manual (create each service separately)

1. **PostgreSQL database:**
   - New → PostgreSQL
   - Name: `githubrepositorysearcher-db`
   - Plan: Starter
   - Database name: `githubrepositorysearcher`
   - User: `githubrepositorysearcher`
   - Create

2. **Web service:**
   - New → Web Service
   - Connect the `Dineshrd0503/GithubRepositorySearcher` GitHub repo
   - Runtime: Docker
   - Dockerfile path: `./githubrepository-searcher/Dockerfile`
   - Docker context: `./githubrepository-searcher`
   - Name: `githubrepositorysearcher`
   - Plan: Starter

---

## Step 3: Configure environment variables on the web service

In the Render dashboard, go to the **web service** → **Environment** and add:

| Key | Value |
|-----|-------|
| `DATABASE_URL` | The **Internal Database URL** from your PostgreSQL service dashboard (see note below) |
| `DB_USERNAME` | The database username (e.g. `githubrepositorysearcher`) |
| `DB_PASSWORD` | The database password shown in the PostgreSQL service dashboard |

> **URL format note:** Render's Internal Database URL usually looks like `postgres://user:pass@host:port/db`. Spring Boot needs `jdbc:postgresql://host:port/db`. If the app fails to connect, set `DATABASE_URL` to the internal URL but replace the leading `postgres://` with `jdbc:postgresql://`.

Save changes — Render will automatically redeploy.

---

## Step 4: Verify the deployment

Wait for the build to finish (Render builds the Docker image, runs Maven, and starts the app). Then test:

**1. Health check / Swagger UI**
```
https://githubrepositorysearcher.onrender.com/swagger-ui.html
```

**2. Search GitHub & persist (POST)**
```
POST https://githubrepositorysearcher.onrender.com/api/github/search
Content-Type: application/json

{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```

**3. Get stored repos with filters (GET)**
```
GET https://githubrepositorysearcher.onrender.com/api/github/repositories?language=Java&minStars=100&sort=stars
```

**4. OpenAPI JSON**
```
https://githubrepositorysearcher.onrender.com/v3/api-docs
```

---

## Step 5: Handover to interviewer

Send the interviewer the live base URL:

```
https://githubrepositorysearcher.onrender.com
```

Include these test endpoints:
- `POST /api/github/search`  — body: `{"query":"spring boot","language":"Java","sort":"stars"}`
- `GET /api/github/repositories?language=Java&minStars=100&sort=stars`
- Swagger UI: `https://githubrepositorysearcher.onrender.com/swagger-ui.html`

---

## Cleanup after the interview

To avoid ongoing charges, go to the Render dashboard and:
1. Delete the `githubrepositorysearcher` web service
2. Delete the `githubrepositorysearcher-db` PostgreSQL database

---

## Project structure (for reference)

```
githubrepository-searcher/
├── Dockerfile                  # Multi-stage Docker build (Maven build + JRE run)
├── pom.xml                     # Spring Boot 3.3.5, Java 21, PostgreSQL, SpringDoc
├── Procfile
├── system.properties           # java.runtime.version=21
├── src/main/java/...           # Controllers, services, entities, DTOs, config
└── src/main/resources/
    └── application.properties  # Render-compatible env-var fallbacks
```
