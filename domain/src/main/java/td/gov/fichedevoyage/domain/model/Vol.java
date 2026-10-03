package td.gov.fichedevoyage.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "vols")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compagnie_id", nullable = false)
    private Compagnie compagnie;

    @Column(name = "numero", length = 20, nullable = false)
    private String numero;

    @Column(name = "jours_operation", length = 7)
    private String joursOperation;

    @Column(name = "heure_arrivee")
    private LocalTime heureArrivee;

    @Column(name = "heure_depart")
    private LocalTime heureDepart;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
