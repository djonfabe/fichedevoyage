package td.gov.fichedevoyage.domain.model;

import td.gov.fichedevoyage.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fiches_voyage", indexes = {
    @Index(name = "idx_fiche_qr_token", columnList = "qr_code_token"),
    @Index(name = "idx_fiche_numero_doc", columnList = "numero_document"),
    @Index(name = "idx_fiche_date_voyage", columnList = "date_voyage")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FicheVoyage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "reference", length = 20, nullable = false, unique = true)
    private String reference;

    @Column(name = "qr_code_token", length = 64, nullable = false, unique = true)
    private String qrCodeToken;

    // ---- IDENTITÉ ----
    @Column(name = "nom", length = 100, nullable = false)
    private String nom;

    @Column(name = "prenoms", length = 150, nullable = false)
    private String prenoms;

    @Column(name = "nom_jeune_fille", length = 100)
    private String nomJeuneFille;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false)
    private Sexe sexe;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", length = 150, nullable = false)
    private String lieuNaissance;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "profession", length = 150, nullable = false)
    private String profession;

    @Column(name = "fonction", length = 150)
    private String fonction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nationalite_id", nullable = false)
    private Pays nationalite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pays_residence_id", nullable = false)
    private Pays paysResidence;

    // ---- DOCUMENT ----
    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false)
    private TypeDocument typeDocument;

    @Column(name = "numero_document", length = 50, nullable = false)
    private String numeroDocument;

    @Column(name = "date_delivrance", nullable = false)
    private LocalDate dateDelivrance;

    @Column(name = "lieu_delivrance", length = 150, nullable = false)
    private String lieuDelivrance;

    // ---- PROVENANCE ----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pays_provenance_id")
    private Pays paysProvenance;

    @Column(name = "ville_provenance", length = 100)
    private String villeProvenance;

    @Column(name = "adresse_provenance", length = 255)
    private String adresseProvenance;

    @Column(name = "contact_provenance", length = 25)
    private String contactProvenance;

    // ---- DESTINATION ----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pays_destination_id")
    private Pays paysDestination;

    @Column(name = "ville_destination", length = 100)
    private String villeDestination;

    @Column(name = "adresse_destination", length = 255)
    private String adresseDestination;

    @Column(name = "contact_destination", length = 25)
    private String contactDestination;

    // ---- SÉJOUR ----
    @Column(name = "date_voyage", nullable = false)
    private LocalDate dateVoyage;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif_voyage")
    private MotifVoyage motifVoyage;

    @Column(name = "duree_sejour_jours")
    private Integer dureeSejour;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_voyage")
    private TypeVoyage typeVoyage;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_hebergement")
    private TypeHebergement typeHebergement;

    // ---- VOL ----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compagnie_id")
    private Compagnie compagnie;

    @Column(name = "numero_vol", length = 20)
    private String numeroVol;

    // ---- ENGAGEMENT & SUIVI ----
    @Column(name = "engagement_accepte", nullable = false)
    private boolean engagementAccepte;

    @Column(name = "ip_soumission", length = 45)
    private String ipSoumission;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutFiche statut = StatutFiche.BROUILLON;

    @Column(name = "scanne_par_agent_id")
    private Long scannéParAgentId;

    @Column(name = "scanne_le")
    private LocalDateTime scannéLe;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
