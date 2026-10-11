## 2026-05-31 - Caching Static Logo in PDF Generation
**Learning:** `FlyingSaucerPdfAdapter` reads `static/img/armoirie.svg` from classpath and performs Base64 encoding on every PDF rendering call. Caching the Base64 string thread-safely eliminates disk/classpath I/O and CPU overhead for logo re-encoding.
**Action:** Use thread-safe lazy initialization or field caching for static template resources used during PDF/document generation.
