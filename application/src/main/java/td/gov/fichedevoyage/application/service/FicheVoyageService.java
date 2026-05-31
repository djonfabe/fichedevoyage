package td.gov.fichedevoyage.application.service;

import td.gov.fichedevoyage.application.dto.*;
import td.gov.fichedevoyage.domain.enums.StatutFiche;
import td.gov.fichedevoyage.domain.model.*;
import td.gov.fichedevoyage.domain.port.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FicheVoyageService {

    private final FicheVoyageRepository ficheRepo;
    private final PaysRepository paysRepo;
    private final CompagnieRepository compagnieRepo;
    private final SecureRandom secureRandom = new SecureRandom();

    public FicheResponse creerFiche(FicheCreationRequest req, String ip, String userAgent) {
        String token = generateToken();
        String reference = ficheRepo.generateNextReference();

        FicheVoyage fiche = FicheVoyage.builder()
                .reference(reference)
                .qrCodeToken(token)
                .nom(req.getNom().trim().toUpperCase())
                .prenoms(req.getPrenoms().trim())
                .nomJeuneFille(req.getNomJeuneFille())
                .sexe(req.getSexe())
                .dateNaissance(req.getDateNaissance())
                .lieuNaissance(req.getLieuNaissance().trim())
                .email(req.getEmail())
                .profession(req.getProfession().trim())
                .fonction(req.getFonction())
                .nationalite(paysRepo.findById(req.getNationaliteId()).orElseThrow())
                .paysResidence(paysRepo.findById(req.getPaysResidenceId()).orElseThrow())
                .typeDocument(req.getTypeDocument())
                .numeroDocument(req.getNumeroDocument().trim().toUpperCase())
                .dateDelivrance(req.getDateDelivrance())
                .lieuDelivrance(req.getLieuDelivrance().trim())
                .paysProvenance(req.getPaysProvenanceId() != null
                        ? paysRepo.findById(req.getPaysProvenanceId()).orElse(null) : null)
                .villeProvenance(req.getVilleProvenance())
                .adresseProvenance(req.getAdresseProvenance())
                .contactProvenance(req.getContactProvenance())
                .paysDestination(req.getPaysDestinationId() != null
                        ? paysRepo.findById(req.getPaysDestinationId()).orElse(null) : null)
                .villeDestination(req.getVilleDestination())
                .adresseDestination(req.getAdresseDestination())
                .contactDestination(req.getContactDestination())
                .dateVoyage(req.getDateVoyage())
                .motifVoyage(req.getMotifVoyage())
                .dureeSejour(req.getDureeSejour())
                .typeVoyage(req.getTypeVoyage())
                .typeHebergement(req.getTypeHebergement())
                .compagnie(req.getCompagnieId() != null
                        ? compagnieRepo.findById(req.getCompagnieId()).orElse(null) : null)
                .numeroVol(req.getNumeroVol())
                .engagementAccepte(req.getEngagementAccepte())
                .ipSoumission(ip)
                .userAgent(userAgent)
                .statut(StatutFiche.VALIDEE)
                .build();

        fiche = ficheRepo.save(fiche);
        return toResponse(fiche);
    }

    @Transactional(readOnly = true)
    public Optional<FicheResponse> rechercherFiche(FicheSearchRequest req) {
        return ficheRepo.findByNumeroDocumentAndDateDelivranceAndDateVoyage(
                req.getNumeroDocument().trim().toUpperCase(),
                req.getDateDelivrance(),
                req.getDateVoyage()
        ).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<FicheResponse> getByToken(String token) {
        return ficheRepo.findByQrCodeToken(token).map(this::toResponse);
    }

    public FicheResponse modifierFiche(String token, FicheCreationRequest req) {
        FicheVoyage fiche = ficheRepo.findByQrCodeToken(token).orElseThrow();
        if (fiche.getStatut() != StatutFiche.VALIDEE) {
            throw new IllegalStateException("La fiche ne peut être modifiée dans son état actuel : " + fiche.getStatut());
        }
        mapRequestToFiche(req, fiche);
        return toResponse(ficheRepo.save(fiche));
    }

    private void mapRequestToFiche(FicheCreationRequest req, FicheVoyage fiche) {
        fiche.setNom(req.getNom().trim().toUpperCase());
        fiche.setPrenoms(req.getPrenoms().trim());
        fiche.setNomJeuneFille(req.getNomJeuneFille());
        fiche.setSexe(req.getSexe());
        fiche.setDateNaissance(req.getDateNaissance());
        fiche.setLieuNaissance(req.getLieuNaissance().trim());
        fiche.setEmail(req.getEmail());
        fiche.setProfession(req.getProfession().trim());
        fiche.setFonction(req.getFonction());
        fiche.setNationalite(paysRepo.findById(req.getNationaliteId()).orElseThrow());
        fiche.setPaysResidence(paysRepo.findById(req.getPaysResidenceId()).orElseThrow());
        fiche.setTypeDocument(req.getTypeDocument());
        fiche.setNumeroDocument(req.getNumeroDocument().trim().toUpperCase());
        fiche.setDateDelivrance(req.getDateDelivrance());
        fiche.setLieuDelivrance(req.getLieuDelivrance().trim());
        fiche.setPaysProvenance(req.getPaysProvenanceId() != null
                ? paysRepo.findById(req.getPaysProvenanceId()).orElse(null) : null);
        fiche.setVilleProvenance(req.getVilleProvenance());
        fiche.setAdresseProvenance(req.getAdresseProvenance());
        fiche.setContactProvenance(req.getContactProvenance());
        fiche.setPaysDestination(req.getPaysDestinationId() != null
                ? paysRepo.findById(req.getPaysDestinationId()).orElse(null) : null);
        fiche.setVilleDestination(req.getVilleDestination());
        fiche.setAdresseDestination(req.getAdresseDestination());
        fiche.setContactDestination(req.getContactDestination());
        fiche.setDateVoyage(req.getDateVoyage());
        fiche.setMotifVoyage(req.getMotifVoyage());
        fiche.setDureeSejour(req.getDureeSejour());
        fiche.setTypeVoyage(req.getTypeVoyage());
        fiche.setTypeHebergement(req.getTypeHebergement());
        fiche.setCompagnie(req.getCompagnieId() != null
                ? compagnieRepo.findById(req.getCompagnieId()).orElse(null) : null);
        fiche.setNumeroVol(req.getNumeroVol());
        fiche.setEngagementAccepte(req.getEngagementAccepte());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private FicheResponse toResponse(FicheVoyage f) {
        return FicheResponse.builder()
                .id(f.getId())
                .reference(f.getReference())
                .qrCodeToken(f.getQrCodeToken())
                .nom(f.getNom())
                .prenoms(f.getPrenoms())
                .nomJeuneFille(f.getNomJeuneFille())
                .sexe(f.getSexe())
                .dateNaissance(f.getDateNaissance())
                .lieuNaissance(f.getLieuNaissance())
                .email(f.getEmail())
                .profession(f.getProfession())
                .fonction(f.getFonction())
                .nationaliteId(f.getNationalite() != null ? f.getNationalite().getId() : null)
                .nationaliteNomFr(f.getNationalite() != null ? f.getNationalite().getNomFr() : null)
                .paysResidenceId(f.getPaysResidence() != null ? f.getPaysResidence().getId() : null)
                .paysResidenceNomFr(f.getPaysResidence() != null ? f.getPaysResidence().getNomFr() : null)
                .typeDocument(f.getTypeDocument())
                .numeroDocument(f.getNumeroDocument())
                .dateDelivrance(f.getDateDelivrance())
                .lieuDelivrance(f.getLieuDelivrance())
                .paysProvenanceId(f.getPaysProvenance() != null ? f.getPaysProvenance().getId() : null)
                .paysProvenanceNomFr(f.getPaysProvenance() != null ? f.getPaysProvenance().getNomFr() : null)
                .villeProvenance(f.getVilleProvenance())
                .paysDestinationId(f.getPaysDestination() != null ? f.getPaysDestination().getId() : null)
                .paysDestinationNomFr(f.getPaysDestination() != null ? f.getPaysDestination().getNomFr() : null)
                .villeDestination(f.getVilleDestination())
                .dateVoyage(f.getDateVoyage())
                .motifVoyage(f.getMotifVoyage())
                .dureeSejour(f.getDureeSejour())
                .typeVoyage(f.getTypeVoyage())
                .typeHebergement(f.getTypeHebergement())
                .compagnieId(f.getCompagnie() != null ? f.getCompagnie().getId() : null)
                .compagnieNom(f.getCompagnie() != null ? f.getCompagnie().getNom() : null)
                .numeroVol(f.getNumeroVol())
                .statut(f.getStatut())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
