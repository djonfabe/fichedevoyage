## 2026-09-09 - Caching Static Classpath Assets in PDF Generation
**Learning:** `FlyingSaucerPdfAdapter` was re-reading and Base64-encoding a 220KB SVG logo (`armoirie.svg`) from the classpath on every PDF generation request, creating unnecessary stream I/O and garbage collection pressure.
**Action:** Always memoize/cache static image resources loaded during PDF rendering to reduce CPU and I/O overhead on PDF generation endpoints.
