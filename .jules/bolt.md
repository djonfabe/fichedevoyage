## 2026-06-01 - JPA Count Queries vs Page Queries for Statistics

**Learning:** Using `findByStatut(statut, PageRequest.of(0, 1)).getTotalElements()` issues a paginated fetch query in addition to/instead of a lean SQL count query, causing extra memory allocation and query execution overhead when only total counts are needed. Replacing this with `long countByStatut(StatutFiche statut)` generates a direct `SELECT COUNT(...)` SQL query.

**Action:** Always prefer explicit `countBy...` repository methods when calculating metrics or dashboard aggregate statistics.
