## 2026-06-01 - Cache Base64 SVG logo in FlyingSaucerPdfAdapter
**Learning:** In Spring Boot PDF generation using FlyingSaucer / Thymeleaf, reading static SVG assets from the classpath on every PDF rendering request adds unnecessary file I/O and Base64 string encoding overhead. Thread-safe lazy caching in memory eliminates this repeated bottleneck.
**Action:** When rendering static assets/templates in PDF/document generation services, cache static Base64 image resources lazily using volatile double-checked locking or static initialization.
