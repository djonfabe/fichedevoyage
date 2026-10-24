# Bolt's Journal - Critical Learnings

## System Overview
- Maven multi-module Spring Boot application (`domain`, `application`, `infrastructure`, `web-public`, `api-admin`, `api-agent`, `admin-front`).
- `admin-front` uses npm (`npm --prefix admin-front run build`).
- System Maven (`mvn test` or `mvn test-compile`) runs backend tests.
