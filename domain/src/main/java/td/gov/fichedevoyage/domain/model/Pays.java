package td.gov.fichedevoyage.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pays")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_iso2", length = 2, nullable = false, unique = true)
    private String codeIso2;

    @Column(name = "code_iso3", length = 3, nullable = false, unique = true)
    private String codeIso3;

    @Column(name = "nom_fr", length = 100, nullable = false)
    private String nomFr;

    @Column(name = "nom_en", length = 100, nullable = false)
    private String nomEn;

    @Column(name = "indicatif_tel", length = 10)
    private String indicatifTel;
}
