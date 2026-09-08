package td.gov.fichedevoyage.application.dto;

import td.gov.fichedevoyage.domain.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FicheCreationRequest {

    // Étape 1 — Identité
    @NotBlank @Size(min = 2, max = 100)
    @Pattern(regexp = "^[\\p{L} ''-]+$")
    private String nom;

    @NotBlank @Size(min = 2, max = 150)
    @Pattern(regexp = "^[\\p{L} ''-]+$")
    private String prenoms;

    @Size(max = 100)
    private String nomJeuneFille;

    @NotNull
    private Sexe sexe;

    @NotNull @Past
    private LocalDate dateNaissance;

    @NotBlank @Size(max = 150)
    private String lieuNaissance;

    @Email
    private String email;

    @NotBlank @Size(max = 150)
    private String profession;

    @Size(max = 150)
    private String fonction;

    @NotNull
    private Long nationaliteId;

    @NotNull
    private Long paysResidenceId;

    // Étape 2 — Document
    @NotNull
    private TypeDocument typeDocument;

    @NotBlank @Size(min = 5, max = 50)
    @Pattern(regexp = "^[A-Za-z0-9]+$")
    private String numeroDocument;

    @NotNull @Past
    private LocalDate dateDelivrance;

    @NotBlank @Size(max = 150)
    private String lieuDelivrance;

    // Étape 3 — Voyage
    private Long paysProvenanceId;

    @Size(max = 100)
    private String villeProvenance;

    @Size(max = 255)
    private String adresseProvenance;

    @Pattern(regexp = "^(\\+[1-9]\\d{6,14})?$")
    private String contactProvenance;

    private Long paysDestinationId;

    @Size(max = 100)
    private String villeDestination;

    @Size(max = 255)
    private String adresseDestination;

    @Pattern(regexp = "^(\\+[1-9]\\d{6,14})?$")
    private String contactDestination;

    @NotNull
    @FutureOrPresent
    private LocalDate dateVoyage;

    private MotifVoyage motifVoyage;

    @Min(0) @Max(365)
    private Integer dureeSejour;

    private TypeVoyage typeVoyage;

    private TypeHebergement typeHebergement;

    private Long compagnieId;

    @Size(max = 20)
    private String numeroVol;

    // Étape 4 — Engagement
    @NotNull @AssertTrue
    private Boolean engagementAccepte;
}
