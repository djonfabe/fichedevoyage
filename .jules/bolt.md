# Bolt's Journal - Critical Learnings

## 2026-10-03 - Reuse thread-safe JJWT JwtParser and eliminate redundant parsing in Spring Security filters
**Learning:** `Jwts.parser().verifyWith(key).build()` constructs a new `JwtParser` instance on every invocation. Calling `isTokenValid` followed by `extractSubject` in `JwtAuthFilter` resulted in building two `JwtParser` instances and parsing/verifying the token signature twice per authenticated HTTP request. Reusing a thread-safe `JwtParser` instance in `JwtService` and extracting claims in a single pass in `JwtAuthFilter` reduces JWT validation overhead by ~50% and eliminates repeated object allocations.
**Action:** Always pre-build `JwtParser` as an immutable field in `JwtService` and parse claims once during request filter validation.
