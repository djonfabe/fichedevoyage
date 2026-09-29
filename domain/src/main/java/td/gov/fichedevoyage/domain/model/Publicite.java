package td.gov.fichedevoyage.domain.model;

import td.gov.fichedevoyage.domain.enums.PositionPublicite;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "publicites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Publicite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titre", length = 200, nullable = false)
    private String titre;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "lien_cible")
    private String lienCible;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private PositionPublicite position;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "nombre_impressions", nullable = false)
    private long nombreImpressions = 0;

    @Column(name = "nombre_clics", nullable = false)
    private long nombreClics = 0;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
