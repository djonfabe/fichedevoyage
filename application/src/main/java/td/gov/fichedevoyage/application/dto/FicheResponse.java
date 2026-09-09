package td.gov.fichedevoyage.application.dto;

import td.gov.fichedevoyage.domain.enums.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FicheResponse {
    private UUID id;
    private String reference;
    private String qrCodeToken;

    // Identité
    private String nom;
    private String prenoms;
    private String nomJeuneFille;
    private Sexe sexe;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String email;
    private String profession;
    private String fonction;
    private Long nationaliteId;
    private String nationaliteNomFr;
    private Long paysResidenceId;
    private String paysResidenceNomFr;

    // Document
    private TypeDocument typeDocument;
    private String numeroDocument;
    private LocalDate dateDelivrance;
    private String lieuDelivrance;

    // Voyage
    private Long paysProvenanceId;
    private String paysProvenanceNomFr;
    private String villeProvenance;
    private Long paysDestinationId;
    private String paysDestinationNomFr;
    private String villeDestination;
    private LocalDate dateVoyage;
    private MotifVoyage motifVoyage;
    private Integer dureeSejour;
    private TypeVoyage typeVoyage;
    private TypeHebergement typeHebergement;
    private Long compagnieId;
    private String compagnieNom;
    private String numeroVol;

    // Statut
    private StatutFiche statut;
    private LocalDateTime createdAt;
}
