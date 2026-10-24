package td.gov.fichedevoyage.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compagnies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Compagnie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", length = 100, nullable = false)
    private String nom;

    @Column(name = "code_iata", length = 2)
    private String codeIata;

    @Column(name = "code_icao", length = 3)
    private String codeIcao;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
