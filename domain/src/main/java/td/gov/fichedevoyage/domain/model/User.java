package td.gov.fichedevoyage.domain.model;

import td.gov.fichedevoyage.domain.enums.RoleUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nom", length = 100, nullable = false)
    private String nom;

    @Column(name = "prenoms", length = 150, nullable = false)
    private String prenoms;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleUser role;

    @JsonIgnore
    @Column(name = "totp_secret")
    private String totpSecret;

    @Column(name = "totp_enabled", nullable = false)
    private boolean totpEnabled = false;

    @Column(name = "poste_frontiere_id")
    private Long posteFrontiereId;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "tentatives_echecs", nullable = false)
    private int tentativesEchecs = 0;

    @Column(name = "verrouille_jusqu_au")
    private LocalDateTime verrouilleJusquAu;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    void onCreate() { this.createdAt = LocalDateTime.now(); }
}
