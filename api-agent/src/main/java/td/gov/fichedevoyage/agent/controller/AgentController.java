package td.gov.fichedevoyage.agent.controller;

import td.gov.fichedevoyage.application.dto.FicheResponse;
import td.gov.fichedevoyage.application.service.FicheVoyageService;
import td.gov.fichedevoyage.domain.enums.StatutFiche;
import td.gov.fichedevoyage.domain.model.FicheVoyage;
import td.gov.fichedevoyage.domain.model.User;
import td.gov.fichedevoyage.infrastructure.persistence.jpa.FicheVoyageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
@PreAuthorize("hasRole('AGENT_FRONTIERE')")
public class AgentController {

    private final FicheVoyageService ficheService;
    private final FicheVoyageJpaRepository ficheRepo;

    @GetMapping("/fiches/{token}")
    public ResponseEntity<FicheResponse> getByQr(@PathVariable String token) {
        return ficheService.getByToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/fiches/{token}/validate")
    public ResponseEntity<Map<String, Object>> validate(
            @PathVariable String token,
            @AuthenticationPrincipal User agent) {

        return ficheRepo.findByQrCodeToken(token).map(fiche -> {
            if (fiche.getStatut() == StatutFiche.SCANNEE) {
                return ResponseEntity.ok(Map.<String, Object>of(
                        "message", "Fiche déjà validée",
                        "reference", fiche.getReference(),
                        "scanneLe", fiche.getScannéLe()
                ));
            }
            fiche.setStatut(StatutFiche.SCANNEE);
            fiche.setScannéParAgentId(agent != null ? agent.getId() : null);
            fiche.setScannéLe(LocalDateTime.now());
            ficheRepo.saveAndFlush(fiche);
            return ResponseEntity.ok(Map.<String, Object>of(
                    "message", "Fiche validée avec succès",
                    "reference", fiche.getReference(),
                    "voyageur", fiche.getNom() + " " + fiche.getPrenoms()
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fiches/today")
    public ResponseEntity<List<FicheVoyage>> fichesToday() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 200,
                org.springframework.data.domain.Sort.by("createdAt").descending());
        return ResponseEntity.ok(ficheRepo.findByDateVoyageBetween(
                LocalDate.now(), LocalDate.now(), pageable).getContent());
    }
}
