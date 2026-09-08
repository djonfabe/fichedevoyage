package td.gov.fichedevoyage.admin.controller;

import td.gov.fichedevoyage.domain.enums.StatutFiche;
import td.gov.fichedevoyage.infrastructure.persistence.jpa.FicheVoyageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERVISEUR')")
public class DashboardController {

    private final FicheVoyageJpaRepository ficheRepo;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("fichesAujourdhui", ficheRepo.countByDateVoyage(LocalDate.now()));
        stats.put("fichesMoisEnCours", ficheRepo.countByDateVoyageBetween(
                LocalDate.now().withDayOfMonth(1), LocalDate.now()));
        stats.put("fichesTotal", ficheRepo.count());
        stats.put("fichesScanneesAujourdhui", ficheRepo.countByStatutAndDateVoyage(
                StatutFiche.SCANNEE, LocalDate.now()));
        // Optimization: Use direct COUNT queries instead of findByStatut(...) Page queries.
        // This avoids unnecessary entity fetching/instantiation and extra query metadata overhead.
        stats.put("fichesBrouillon", ficheRepo.countByStatut(StatutFiche.BROUILLON));
        stats.put("fichesValidees", ficheRepo.countByStatut(StatutFiche.VALIDEE));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/fiches")
    public ResponseEntity<?> listFiches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (statut != null && !statut.isBlank()) {
            return ResponseEntity.ok(ficheRepo.findByStatut(StatutFiche.valueOf(statut), pageable));
        }
        if (dateDebut != null && dateFin != null) {
            return ResponseEntity.ok(ficheRepo.findByDateVoyageBetween(dateDebut, dateFin, pageable));
        }
        return ResponseEntity.ok(ficheRepo.findAll(pageable));
    }

    @PatchMapping("/fiches/{id}/valider")
    public ResponseEntity<?> valider(@PathVariable UUID id) {
        return ficheRepo.findFicheById(id).map(f -> {
            f.setStatut(StatutFiche.VALIDEE);
            f.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(ficheRepo.saveAndFlush(f));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/fiches/{id}/annuler")
    public ResponseEntity<?> annuler(@PathVariable UUID id) {
        return ficheRepo.findFicheById(id).map(f -> {
            f.setStatut(StatutFiche.ANNULEE);
            f.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(ficheRepo.saveAndFlush(f));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats/monthly")
    public ResponseEntity<?> statsMonthly(
            @RequestParam(required = false) Integer year) {
        int effectiveYear = (year != null) ? year : java.time.Year.now().getValue();
        return ResponseEntity.ok(ficheRepo.countByMonth(effectiveYear));
    }
}
