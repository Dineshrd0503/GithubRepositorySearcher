# GitHub Repository Searcher — Postman Testing Evidence

This document captures the live API testing performed with Postman against the running Spring Boot application. It includes the exact request payloads, URLs, and JSON responses received, suitable for interview/demo review.

---

## 1. Project Summary

| Item | Value |
|------|-------|
| Project name | githubrepository-searcher |
| Package | `com.freightfox` |
| Java version | 21 |
| Spring Boot version | 3.3.5 |
| Server URL | `http://localhost:8080` |
| Database | PostgreSQL `githubrepositorysearcher` (localhost:5432) |
| Datasource username | `postgres` |
| Table auto-created | `repositories` (via Hibernate `ddl-auto=update`) |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |

---

## 2. Database Configuration

**File:** `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/githubrepositorysearcher
spring.datasource.username=postgres
spring.datasource.password=1234@
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
```

**SQL to create the database (run once in PostgreSQL):**

```sql
CREATE DATABASE githubrepositorysearcher;
```

---

## 3. API Endpoint 1 — Search GitHub Repositories & Persist

**Method:** `POST`  
**URL:** `http://localhost:8080/api/github/search`  
**Header:** `Content-Type: application/json`  
**Body:** raw JSON

### Test Case A — Query: "react"

**Request Body:**
```json
{
  "query": "react",
  "language": null,
  "sort": "stars"
}
```

**Response (200 OK):**
```json
{
  "message": "Repositories fetched and saved successfully",
  "repositories": [
    {
      "id": 1,
      "name": "rea",
      "description": "Reverse engineer anything with agents, from app behavior down to native binaries.",
      "owner": "morluto",
      "language": "TypeScript",
      "stars": 470954,
      "forks": 115011,
      "lastUpdated": "2026-11-20T14:06:10"
    },
    {
      "id": 2,
      "name": "freeCodeCamp",
      "description": "freeCodeCamp.org's open-source codebase and curriculum. Learn math, programming, and computer science for free.",
      "owner": "freeCodeCamp",
      "language": "TypeScript",
      "stars": 459382,
      "forks": 47478,
      "lastUpdated": "2026-11-20T14:05:30"
    },
    {
      "id": 4,
      "name": "react",
      "description": "The library for web and native user interfaces.",
      "owner": "react",
      "language": "JavaScript",
      "stars": 252833,
      "forks": 52814,
      "lastUpdated": "2026-11-20T13:54:30"
    },
    {
      "id": 35,
      "name": "ui-ux-pro-max-skill",
      "description": "An AI skill that provides design intelligence for building professional UI/UX across multiple platforms.",
      "owner": "nextlevelbuilder",
      "language": "Python",
      "stars": 152029,
      "forks": 16048,
      "lastUpdated": "2026-11-20T14:03:54"
    },
    {
      "id": 36,
      "name": "next.js",
      "description": "The React Framework",
      "owner": "vercel",
      "language": "JavaScript",
      "stars": 143625,
      "forks": 32807,
      "lastUpdated": "2026-11-20T13:54:40"
    },
    {
      "id": 37,
      "name": "ui",
      "description": "Composable, accessible components with thoughtful defaults.",
      "owner": "shadcn-ui",
      "language": "TypeScript",
      "stars": 128630,
      "forks": 11424,
      "lastUpdated": "2026-11-20T13:58:16"
    },
    {
      "id": 38,
      "name": "react-native",
      "description": "A framework for building native applications using React",
      "owner": "react",
      "language": "C++",
      "stars": 126976,
      "forks": 25505,
      "lastUpdated": "2026-11-20T13:01:47"
    },
    {
      "id": 40,
      "name": "create-react-app",
      "description": "Set up a modern web app by running one command.",
      "owner": "react",
      "language": "JavaScript",
      "stars": 103222,
      "forks": 26837,
      "lastUpdated": "2026-11-20T13:28:59"
    },
    {
      "id": 41,
      "name": "ant-design",
      "description": "An enterprise-class UI design language and React UI library",
      "owner": "ant-design",
      "language": "TypeScript",
      "stars": 100313,
      "forks": 54623,
      "lastUpdated": "2026-11-20T11:55:35"
    },
    {
      "id": 42,
      "name": "material-ui",
      "description": "Material UI: Comprehensive React component library that implements Google's Material Design.",
      "owner": "mui",
      "language": "JavaScript",
      "stars": 99370,
      "forks": 32485,
      "lastUpdated": "2026-11-20T11:50:08"
    }
  ]
}
```

**Observations:**
- `message` confirms repositories were fetched and saved.
- Each repository contains: `id`, `name`, `description`, `owner`, `language`, `stars`, `forks`, `lastUpdated`.
- The GitHub `language:null` filter means no language restriction was applied.

---

### Test Case B — Query: "typescript"

**Request Body:**
```json
{
  "query": "typescript",
  "language": null,
  "sort": "stars"
}
```

**Response (200 OK) — excerpt:**
```json
{
  "message": "Repositories fetched and saved successfully",
  "repositories": [
    {
      "id": 1,
      "name": "rea",
      "language": "TypeScript",
      "stars": 470959,
      "forks": 115012,
      "lastUpdated": "2026-11-20T14:09:02"
    },
    {
      "id": 60,
      "name": "n8n",
      "language": "TypeScript",
      "stars": 211511,
      "forks": 61606,
      "lastUpdated": "2026-11-20T13:50:52"
    },
    {
      "id": 61,
      "name": "vscode",
      "language": "TypeScript",
      "stars": 195699,
      "forks": 43895,
      "lastUpdated": "2026-11-20T13:38:47"
    },
    {
      "id": 62,
      "name": "prompts.chat",
      "language": "MDX",
      "stars": 177954,
      "forks": 22695,
      "lastUpdated": "2026-11-20T14:05:37"
    },
    {
      "id": 63,
      "name": "langchain",
      "language": "Python",
      "stars": 149964,
      "forks": 25159,
      "lastUpdated": "2026-11-20T13:45:06"
    },
    {
      "id": 16,
      "name": "TypeScript",
      "language": "Go",
      "stars": 111865,
      "forks": 14696,
      "lastUpdated": "2026-11-20T08:59:37"
    },
    {
      "id": 18,
      "name": "deno",
      "language": "Rust",
      "stars": 109115,
      "forks": 7494,
      "lastUpdated": "2026-11-20T12:37:37"
    },
    {
      "id": 70,
      "name": "strapi",
      "language": "TypeScript",
      "stars": 73527,
      "forks": 10036,
      "lastUpdated": "2026-11-20T13:19:43"
    },
    {
      "id": 74,
      "name": "prettier",
      "language": "JavaScript",
      "stars": 52438,
      "forks": 5040,
      "lastUpdated": "2026-11-20T12:17:56"
    }
  ]
}
```

**Observations:**
- Existing repository `id: 1` ("rea") was **updated** (stars changed from `470954` → `470959`, `lastUpdated` changed) instead of being duplicated — confirming **upsert semantics**.
- New repositories (`n8n`, `vscode`, `prompts.chat`, `langchain`, etc.) were inserted with new IDs (`60`–`74`).

---

## 4. API Endpoint 2 — Retrieve Stored Repositories

**Method:** `GET`  
**URL:** `http://localhost:8080/api/github/repositories`

### Test Case — Get All Stored Repositories

**Request URL:**
```
http://localhost:8080/api/github/repositories?query=react
```

**Response (200 OK):**
Returns a JSON array of all stored repository objects with fields:
`id`, `name`, `description`, `owner`, `language`, `stars`, `forks`, `lastUpdated`.

Sample entries from the response:
```json
[
  {
    "id": 1,
    "name": "rea",
    "language": "TypeScript",
    "stars": 470954,
    "forks": 115011,
    "lastUpdated": "2026-11-20T14:06:10"
  },
  {
    "id": 4,
    "name": "react",
    "language": "JavaScript",
    "stars": 252833,
    "forks": 52814,
    "lastUpdated": "2026-11-20T13:54:30"
  },
  {
    "id": 31,
    "name": "boardEx1",
    "language": "Java",
    "stars": 0,
    "forks": 0,
    "lastUpdated": "2023-06-29T03:20:32"
  },
  ...
]
```

**Observations:**
- The endpoint returns all repositories currently persisted in PostgreSQL.
- Includes repositories from both "react" and "typescript" search batches.
- Repositories with `stars: 0` and older `lastUpdated` dates were already present in the database.

---

### Test Case — Filter by Language + Min Stars (case-insensitive params)

**Request URL:**
```
http://localhost:8080/api/github/repositories?language=TypeScript&minstars=10000
```

**Response (200 OK):**
Returns a JSON array of stored repositories filtered by `language=TypeScript` and `minstars=10000`. Note that `minstars` (lowercase) works because the controller accepts both `minStars` and `minstars` casings.

```json
[
  {
    "id": 1,
    "name": "rea",
    "description": "Reverse engineer anything with agents, from app behavior down to native binaries.",
    "owner": "morluto",
    "language": "TypeScript",
    "stars": 470959,
    "forks": 115012,
    "lastUpdated": "2026-11-20T14:09:02"
  },
  {
    "id": 2,
    "name": "freeCodeCamp",
    "description": "freeCodeCamp.org's open-source codebase and curriculum. Learn math, programming, and computer science for free.",
    "owner": "freeCodeCamp",
    "language": "TypeScript",
    "stars": 459382,
    "forks": 47478,
    "lastUpdated": "2026-11-20T14:05:30"
  },
  {
    "id": 5,
    "name": "vue",
    "description": "This is the repo for Vue 2. For Vue 3, go to https://github.com/vuejs/core",
    "owner": "vuejs",
    "language": "TypeScript",
    "stars": 212514,
    "forks": 34420,
    "lastUpdated": "2026-11-20T07:16:08"
  },
  {
    "id": 60,
    "name": "n8n",
    "description": "Fair-code workflow automation platform with native AI capabilities. Combine visual building with custom code, self-host or cloud, 400+ integrations.",
    "owner": "n8n-io",
    "language": "TypeScript",
    "stars": 211511,
    "forks": 61606,
    "lastUpdated": "2026-11-20T13:50:52"
  },
  {
    "id": 61,
    "name": "vscode",
    "description": "Visual Studio Code",
    "owner": "microsoft",
    "language": "TypeScript",
    "stars": 195699,
    "forks": 43895,
    "lastUpdated": "2026-11-20T13:38:47"
  },
  {
    "id": 37,
    "name": "ui",
    "description": "Composable, accessible components with thoughtful defaults. Build your own component library with code you can customize, extend, and make your own.",
    "owner": "shadcn-ui",
    "language": "TypeScript",
    "stars": 128630,
    "forks": 11424,
    "lastUpdated": "2026-11-20T13:58:16"
  },
  {
    "id": 13,
    "name": "immich",
    "description": "High performance self-hosted photo and video management solution.",
    "owner": "immich-app",
    "language": "TypeScript",
    "stars": 119717,
    "forks": 7665,
    "lastUpdated": "2026-11-20T13:50:41"
  },
  {
    "id": 20,
    "name": "angular",
    "description": "Deliver web apps with confidence 🚀",
    "owner": "angular",
    "language": "TypeScript",
    "stars": 101471,
    "forks": 28194,
    "lastUpdated": "2026-11-20T13:51:55"
  },
  {
    "id": 21,
    "name": "playwright",
    "description": "Playwright is a framework for Web Testing and Automation. It allows testing Chromium, Firefox and WebKit with a single API. ",
    "owner": "microsoft",
    "language": "TypeScript",
    "stars": 100635,
    "forks": 6935,
    "lastUpdated": "2026-11-20T07:58:57"
  },
  {
    "id": 41,
    "name": "ant-design",
    "description": "An enterprise-class UI design language and React UI library",
    "owner": "ant-design",
    "language": "TypeScript",
    "stars": 100313,
    "forks": 54623,
    "lastUpdated": "2026-11-20T11:55:35"
  },
  {
    "id": 25,
    "name": "puppeteer",
    "description": "JavaScript API for Chrome and Firefox",
    "owner": "puppeteer",
    "language": "TypeScript",
    "stars": 95874,
    "forks": 9609,
    "lastUpdated": "2026-11-20T07:40:13"
  },
  {
    "id": 27,
    "name": "storybook",
    "description": "Storybook is the industry standard workshop for building, documenting, and testing UI components in isolation",
    "owner": "storybookjs",
    "language": "TypeScript",
    "stars": 91662,
    "forks": 10600,
    "lastUpdated": "2026-11-20T13:51:36"
  },
  {
    "id": 28,
    "name": "mermaid",
    "description": "Generation of diagrams like flowcharts or sequence diagrams from text in a similar manner as markdown",
    "owner": "mermaid-js",
    "language": "TypeScript",
    "stars": 91177,
    "forks": 9517,
    "lastUpdated": "2026-11-20T12:58:07"
  },
  {
    "id": 43,
    "name": "NextChat",
    "description": "✨ Zero-config AI chat assistant. No API key needed — sign up and instantly chat with GPT-5, Claude 4, Gemini 2.5, DeepSeek & 100+ top models. Pay-as-you-go saves you more. Available on Web, iOS, macOS, Android, Linux, Windows.",
    "owner": "ChatGPTNextWeb",
    "language": "TypeScript",
    "stars": 88984,
    "forks": 58470,
    "lastUpdated": "2026-11-20T12:22:47"
  },
  {
    "id": 64,
    "name": "hyperframes",
    "description": "AI video you can actually edit. HyperFrames is a free, open-source AI video editor. Describe any video and get a real project you can change down to the frame.",
    "owner": "heygen-com",
    "language": "TypeScript",
    "stars": 87287,
    "forks": 8127,
    "lastUpdated": "2026-11-20T14:07:02"
  },
  {
    "id": 44,
    "name": "realworld",
    "description": "\"The mother of all demo apps\" — Exemplary fullstack Medium.com clone powered by React, Angular, Node, Django, and many more",
    "owner": "realworld-apps",
    "language": "TypeScript",
    "stars": 84395,
    "forks": 7702,
    "lastUpdated": "2026-11-20T12:23:09"
  },
  {
    "id": 66,
    "name": "ruflo",
    "description": "🌊 The original agent harness. Deploy intelligent multi-player swarms, coordinate autonomous workflows, and build conversational AI systems. Features adaptive memory, self-learning intelligence, federation, vector RAG integration, and native Claude Code / Codex / Hermes and many more Integrated",
    "owner": "ruvnet",
    "language": "TypeScript",
    "stars": 82142,
    "forks": 9697,
    "lastUpdated": "2026-11-20T13:52:13"
  },
  {
    "id": 30,
    "name": "nest",
    "description": "A progressive TypeScript framework for Node.js backends: from a small API in one file to efficient, scalable, enterprise-grade systems 🚀",
    "owner": "nestjs",
    "language": "TypeScript",
    "stars": 77116,
    "forks": 8737,
    "lastUpdated": "2026-11-20T13:15:42"
  },
  {
    "id": 48,
    "name": "remotion",
    "description": "🎥      Make videos programmatically with React",
    "owner": "remotion-dev",
    "language": "TypeScript",
    "stars": 74508,
    "forks": 6324,
    "lastUpdated": "2026-11-20T13:59:42"
  },
  {
    "id": 70,
    "name": "strapi",
    "description": "🚀 Strapi is the leading open-source headless CMS. It’s 100% JavaScript/TypeScript, fully customizable, and developer-first.",
    "owner": "strapi",
    "language": "TypeScript",
    "stars": 73527,
    "forks": 10036,
    "lastUpdated": "2026-11-20T13:19:43"
  },
  {
    "id": 71,
    "name": "oh-my-openagent",
    "description": "OmO: Just type \"mass ulw\" keyword with your prompt. Now you are the master of graph engineering.",
    "owner": "code-yeongyu",
    "language": "TypeScript",
    "stars": 71022,
    "forks": 5843,
    "lastUpdated": "2026-11-20T13:54:43"
  },
  {
    "id": 49,
    "name": "docusaurus",
    "description": "Easy to maintain open source documentation websites.",
    "owner": "meta",
    "language": "TypeScript",
    "stars": 66775,
    "forks": 10142,
    "lastUpdated": "2026-11-20T08:36:24"
  },
  {
    "id": 52,
    "name": "plane",
    "description": "🔥🔥🔥 Open-source Jira, Linear, Monday, and ClickUp alternative. Plane is a modern project management platform to manage tasks, sprints, docs, and triage.",
    "owner": "makeplane",
    "language": "TypeScript",
    "stars": 62849,
    "forks": 6489,
    "lastUpdated": "2026-11-20T13:57:07"
  },
  {
    "id": 53,
    "name": "twenty",
    "description": "The open alternative to Salesforce, designed for AI.",
    "owner": "twentyhq",
    "language": "TypeScript",
    "stars": 61246,
    "forks": 10156,
    "lastUpdated": "2026-11-20T13:56:01"
  },
  {
    "id": 59,
    "name": "zustand",
    "description": "🐻 Bear necessities for state management in React",
    "owner": "pmndrs",
    "language": "TypeScript",
    "stars": 58963,
    "forks": 2417,
    "lastUpdated": "2026-11-20T11:50:02"
  },
  {
    "id": 54,
    "name": "appwrite",
    "description": "The open-source cloud for agents & devs. Including Auth, Databases, Storage, Functions, Messaging, Hosting, Realtime, WAF and more",
    "owner": "appwrite",
    "language": "TypeScript",
    "stars": 58427,
    "forks": 5908,
    "lastUpdated": "2026-11-20T13:43:40"
  },
  {
    "id": 55,
    "name": "react-router",
    "description": "Declarative routing for React",
    "owner": "remix-run",
    "language": "TypeScript",
    "stars": 56645,
    "forks": 10977,
    "lastUpdated": "2026-11-20T08:38:06"
  },
  {
    "id": 57,
    "name": "Flowise",
    "description": "Build AI Agents, Visually",
    "owner": "FlowiseAI",
    "language": "TypeScript",
    "stars": 55581,
    "forks": 25098,
    "lastUpdated": "2026-11-20T12:56:08"
  },
  {
    "id": 58,
    "name": "expo",
    "description": "An open-source framework for making universal native apps with React. Expo runs on Android, iOS, and the web.",
    "owner": "expo",
    "language": "TypeScript",
    "stars": 53689,
    "forks": 15214,
    "lastUpdated": "2026-11-20T11:13:21"
  },
  {
    "id": 73,
    "name": "ionic-framework",
    "description": "A powerful cross-platform UI toolkit for building native-quality iOS, Android, and Progressive Web Apps with HTML, CSS, and JavaScript.",
    "owner": "ionic-team",
    "language": "TypeScript",
    "stars": 52772,
    "forks": 13292,
    "lastUpdated": "2026-11-20T13:53:54"
  }
]
```

**Observations:**
- All returned repositories have `language: "TypeScript"` — the language filter worked.
- All returned repositories have `stars >= 10000` — the `minstars` filter worked (lowercase casing accepted).
- Results are sorted by `stars` descending (default sort).
- The `minstars` parameter (lowercase **s**) is accepted thanks to the case-insensitive parameter fix in `RepositoryController`.

---

### Supported Query Parameters (optional)

| Parameter | Example | Description |
|-----------|---------|-------------|
| `language` | `JavaScript` | Filter by programming language (case-insensitive) |
| `minStars` | `100000` | Minimum stars count |
| `sort` | `stars` | Sort by `stars`, `forks`, or `updated` (default `stars`) |

**Example filtered GET:**
```
http://localhost:8080/api/github/repositories?language=JavaScript&minStars=100000&sort=stars
```

---

## 5. Key Features Demonstrated

| Feature | Evidence |
|---------|----------|
| GitHub REST API integration | Successful `POST /api/github/search` calls to `api.github.com/search/repositories` |
| PostgreSQL persistence | Repositories saved and retrievable via `GET /api/github/repositories` |
| Upsert (no duplicates) | Repository `id: 1` ("rea") updated in place when searched again ("typescript" query) |
| Swagger UI documentation | Available at `http://localhost:8080/swagger-ui.html` |
| Spring Boot DevTools | Auto-restart enabled; LiveReload on port 35729 |
| Validation | Blank `query` or invalid `sort` returns `400 Bad Request` with structured `ErrorResponse` |
| Error handling | GitHub rate limits / API failures return structured `502` errors |

---

## 6. How to Reproduce

1. **Create the database:**
   ```sql
   CREATE DATABASE githubrepositorysearcher;
   ```

2. **Run the application:**
   ```bash
   cd "D:\freightfox assignment\githubrepository-searcher"
   mvn spring-boot:run
   ```

3. **Open Postman** and test:
   - `POST http://localhost:8080/api/github/search` with JSON body
   - `GET http://localhost:8080/api/github/repositories`

4. **View API docs:**
   - `http://localhost:8080/swagger-ui.html`

---

**Tested on:** November 20, 2026  
**Tested via:** Postman  
**Application status:** Running on `http://localhost:8080`
