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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReferenceController {

    private final PaysRepository paysRepo;
    private final CompagnieRepository compagnieRepo;
    private final VolJpaRepository volRepo;

    @GetMapping("/pays")
    public ResponseEntity<List<Pays>> listPays(@RequestParam(defaultValue = "fr") String lang) {
        List<Pays> list = "en".equalsIgnoreCase(lang)
                ? paysRepo.findAllOrderByNomEn()
                : paysRepo.findAllOrderByNomFr();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/compagnies")
    public ResponseEntity<List<Compagnie>> listCompagnies() {
        return ResponseEntity.ok(compagnieRepo.findAllActive());
    }

    @GetMapping("/vols")
    public ResponseEntity<?> listVols(@RequestParam(required = false) Long compagnie) {
        if (compagnie != null) {
            return ResponseEntity.ok(volRepo.findByCompagnieIdAndActiveTrue(compagnie));
        }
        return ResponseEntity.ok(volRepo.findByActiveTrueOrderByNumeroAsc());
    }
}
