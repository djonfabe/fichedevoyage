package td.gov.fichedevoyage.web.controller;

import td.gov.fichedevoyage.domain.model.Compagnie;
import td.gov.fichedevoyage.domain.model.Pays;
import td.gov.fichedevoyage.domain.port.CompagnieRepository;
import td.gov.fichedevoyage.domain.port.PaysRepository;
import td.gov.fichedevoyage.infrastructure.persistence.jpa.VolJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReferenceController {

    private final PaysRepository paysRepo;
    private final CompagnieRepository compagnieRepo;
    private final VolJpaRepository volRepo;

    // Performance optimization: Cache static country and airline lists in memory.
    // Country and airline reference data rarely changes at runtime, so caching avoids
    // repeated database roundtrips on public wizard/API calls.
    private final Map<String, List<Pays>> paysCache = new ConcurrentHashMap<>();
    private volatile List<Compagnie> compagniesCache;

    @GetMapping("/pays")
    public ResponseEntity<List<Pays>> listPays(@RequestParam(defaultValue = "fr") String lang) {
        String key = "en".equalsIgnoreCase(lang) ? "en" : "fr";
        List<Pays> list = paysCache.computeIfAbsent(key, k -> "en".equals(k)
                ? paysRepo.findAllOrderByNomEn()
                : paysRepo.findAllOrderByNomFr());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/compagnies")
    public ResponseEntity<List<Compagnie>> listCompagnies() {
        if (compagniesCache == null) {
            synchronized (this) {
                if (compagniesCache == null) {
                    compagniesCache = compagnieRepo.findAllActive();
                }
            }
        }
        return ResponseEntity.ok(compagniesCache);
    }

    @GetMapping("/vols")
    public ResponseEntity<?> listVols(@RequestParam(required = false) Long compagnie) {
        if (compagnie != null) {
            return ResponseEntity.ok(volRepo.findByCompagnieIdAndActiveTrue(compagnie));
        }
        return ResponseEntity.ok(volRepo.findByActiveTrueOrderByNumeroAsc());
    }
}
