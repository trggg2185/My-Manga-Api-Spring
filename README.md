# My Manga App

Backend REST API for a manga-reading and translation-group platform.

The application provides:

- User registration, login, JWT refresh, introspection, and logout
- Role- and permission-based authorization
- Translation-group creation, membership, and approval workflows
- Manga, category, chapter, and page management
- Image upload and optimization using Cloudflare R2
- Redis-backed rate limiting, token invalidation, and chapter-view buffering
- Scheduled cleanup and view-flush jobs

## Tech stack

- Java 21
- Spring Boot 3.5.14
- Spring Web, Validation, Security, AOP, Actuator
- Spring Data JPA with MySQL
- Spring Data Redis
- Nimbus JOSE JWT 10.5
- MapStruct 1.6.3
- Lombok 1.18.38
- AWS SDK S3 2.29.15 for Cloudflare R2
- Thumbnailator and WebP ImageIO for image processing
- Maven Wrapper

## Project structure

```text
src/main/java/com/example/mymangaapp/mymangaapp/
├── controller/       REST controllers
├── dto/
│   ├── request/      Validated request DTOs
│   ├── response/     Response DTOs and wrappers
│   └── transgroup/   Translation-group DTOs
├── entity/           JPA entities and relationships
├── enums/            Domain enums
├── exception/        AppException, response codes, global handler
├── mapper/           MapStruct mapper interfaces
├── repository/       Spring Data JPA repositories
├── scheduler/        Scheduled background jobs
├── security/         JWT, authorization, CORS, Redis, and R2 config
└── service/          Business logic

src/main/resources/
├── application.yaml  Application and environment configuration
└── rate_limit.lua    Atomic Redis fixed-window rate-limit script
```

The configured servlet context path is `/mymangaapp`, and the default server port is
`8080`.

## Prerequisites

- JDK 21
- MySQL
- Redis
- Cloudflare R2 bucket and credentials

## Configuration

Copy `.env.example` to `.env` or export the variables in the process environment.
The application reads configuration from `src/main/resources/application.yaml`.

Required variables include:

| Variable | Purpose |
| --- | --- |
| `DB_URL` | MySQL JDBC URL |
| `DB_PASSWORD` | MySQL password |
| `SIGNER_KEY` | JWT HMAC signing key |
| `ACCESS_TOKEN_VALIDITY_IN_SECONDS` | Access-token lifetime |
| `REFRESHABLE_DURATION_IN_SECONDS` | Refresh-token validity window |
| `REDIS_HOST` | Redis host; defaults to `localhost` |
| `REDIS_PORT` | Redis port; defaults to `6379` |
| `REDIS_PASSWORD` | Redis password, if required |
| `REDIS_DATABASE` | Redis logical database; defaults to `0` |
| `CLOUDFLARE_R2_ACCESS_KEY_ID` | R2 access key |
| `CLOUDFLARE_R2_SECRET_ACCESS_KEY` | R2 secret key |
| `CLOUDFLARE_R2_ENDPOINT_URL` | R2 S3-compatible endpoint |
| `CLOUDFLARE_R2_BUCKET_NAME` | R2 bucket name |
| `CLOUDFLARE_R2_PUBLIC_URL` | Public URL prefix for uploaded files |
| `INIT_ADMIN_PASSWORD` | Initial admin password |
| `INIT_USER1_PASSWORD` | Initial seed user password |
| `INIT_USER2_PASSWORD` | Initial seed user password |
| `INIT_USER3_PASSWORD` | Initial seed user password |

`.env` is ignored by Git. Never commit real credentials, JWT keys, passwords, or
tokens.

## Running the application

From the repository root on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Build the application:

```powershell
.\mvnw.cmd clean package
```

Run tests:

```powershell
.\mvnw.cmd test
```

The current test suite contains a Spring application context-load smoke test. The
application requires its external configuration and services to be available when
the context is started.

## API conventions

### Base URL

When running locally, the base URL is:

```text
http://localhost:8080/mymangaapp
```

### Main endpoint groups

| Area | Endpoints |
| --- | --- |
| Authentication | `/auth/register`, `/auth/login`, `/auth/introspect`, `/auth/logout`, `/auth/refresh` |
| Users | `/users/me`, `/users/me/password`, `/admin/users` |
| Manga | `/mangas`, `/mangas/{id}`, `/transgroups/{groupId}/mangas`, `/admin/mangas` |
| Chapters/pages | `/mangas/{mangaId}/chapters`, `/chapters/{chapterId}/pages` |
| Translation groups | `/transgroups`, `/admin/transgroups`, join and creation requests |
| Categories | `/categories`, `/admin/categories` |
| Roles/permissions | `/admin/roles`, `/admin/permissions` |
| Files | `/files/upload`, `/files/upload-multi` |

### Response wrapper

Successful and handled error responses use `ApiResponse<T>`:

```json
{
  "code": "0000",
  "message": "Thành công!",
  "result": {}
}
```

Error codes are defined in `ResponseCode` and converted by
`GlobalExceptionHandler`. Expected application failures should be represented by
`AppException` with an appropriate `ResponseCode`.

### Pagination

Paginated endpoints use `page`, `size`, and `sortBy` query parameters. The service
layer creates a Spring `PageRequest`, and `PaginatedResponse<T>` returns:

```json
{
  "currentPage": 1,
  "pageSize": 20,
  "totalPages": 4,
  "totalElements": 75,
  "content": []
}
```

The API exposes one-based `currentPage` values even though Spring Data internally
uses zero-based page indexes.

### Authentication

Send the access token with the standard bearer header:

```http
Authorization: Bearer <access-token>
```

The API is stateless. Public authentication endpoints and public manga, group,
chapter/page, and category reads do not require a token. `/admin/**` requires the
`ADMIN` role; additional ownership and group-leader checks are enforced at the
service layer with `@PreAuthorize`.

## Data model

The main entities are:

- `User`, `Role`, and `Permission`
- `TransGroup`
- `Manga`, `Category`, `Chapter`, and `Page`
- `GroupCreationRequest` and `GroupJoinRequest`

All entities inherit auditing fields from `BaseEntity`. Manga-to-chapter and
chapter-to-page relationships use cascade and orphan removal. Chapters are unique
within a manga by `(manga_id, chapter_index)`, and pages are unique within a chapter
by `(chapter_id, page_number)`.

Hibernate is configured with `ddl-auto: update` for the current project setup. Treat
this as a development convenience rather than a production migration strategy.

## Storage and background processing

### Cloudflare R2

Uploaded images are validated, resized/optimized to WebP where appropriate, and
stored in the configured R2 bucket. Temporary uploads use the `tmp/` prefix and are
returned as public URLs.

### Redis

Redis is used for:

- Invalidated JWT IDs after logout
- Atomic fixed-window API rate limiting through `rate_limit.lua`
- Chapter-view locks and pending view counters

### Scheduled jobs

- Every five minutes: flush buffered chapter views from Redis to MySQL in batches.
- Every hour: delete R2 objects under `tmp/` older than three hours.
- Every day at 03:00: a scheduled failed-upload cleanup hook exists but is not yet
  implemented.

At startup, `InitApplicationConfig` creates missing roles, permissions, default
categories, initial users, and an initial admin translation group where applicable.

## Development conventions

- Keep business logic and authorization in services.
- Use request and response DTOs; do not expose JPA entities directly from controllers.
- Use MapStruct mappers for entity/DTO conversion.
- Return `ApiResponse<T>` from API controllers.
- Use `AppException` and `ResponseCode` for expected errors.
- Prefer lazy JPA relationships and explicit `@EntityGraph` queries when related data
  is required.
- Use Lombok constructor injection and `@FieldDefaults` consistently with existing
  code.
- Keep many-to-many relationships as `Set` collections.
- Do not log credentials, bearer tokens, passwords, or signing keys.
- Do not edit generated files under `target/`.

For more detailed contributor guidance, see [`AGENTS.md`](AGENTS.md).

## Known limitations and inconsistencies

The following are current project characteristics, not corrected by this README update:

- The README's former database sketch described `manga.genres`, while the entity model
  uses the `Category` many-to-many relationship.
- Both JJWT and Nimbus JWT dependencies are declared, but the implementation uses
  Nimbus through `JwtUtils`.
- `SecutityConfig` retains its existing misspelled class/file name.
- The scheduled failed-upload cleanup method is currently a placeholder.
- Rate-limit evaluation currently fails open if Redis evaluation throws an exception.
- The existing smoke test depends on external application configuration.
