package td.gov.fichedevoyage.admin.controller;

import td.gov.fichedevoyage.domain.model.Compagnie;
import td.gov.fichedevoyage.domain.port.CompagnieRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/compagnies")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERVISEUR')")
public class CompagnieAdminController {

    private final CompagnieRepository compagnieRepo;

    @GetMapping
    public List<Compagnie> list() { return compagnieRepo.findAllActive(); }

    @PostMapping
    public ResponseEntity<Compagnie> create(@Valid @RequestBody Compagnie compagnie) {
        compagnie.setActive(true);
        return ResponseEntity.ok(compagnieRepo.save(compagnie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compagnie> update(@PathVariable Long id, @Valid @RequestBody Compagnie data) {
        return compagnieRepo.findById(id).map(c -> {
            c.setNom(data.getNom());
            c.setCodeIata(data.getCodeIata());
            c.setCodeIcao(data.getCodeIcao());
            c.setActive(data.isActive());
            return ResponseEntity.ok(compagnieRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compagnieRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
