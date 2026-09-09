package td.gov.fichedevoyage.infrastructure.init;

import td.gov.fichedevoyage.domain.enums.RoleUser;
import td.gov.fichedevoyage.domain.model.User;
import td.gov.fichedevoyage.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@Profile("!prod")
@RequiredArgsConstructor
public class DevDataInitializer {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner seedDevUsers() {
        return args -> {
            seed("admin@fichedevoyage.td",      "Admin@2026",  "MAHAMAT",   "Idriss Admin",      RoleUser.ADMIN);
            seed("superviseur@fichedevoyage.td", "Super@2026",  "OUMAR",    "Fatimé Superviseur", RoleUser.SUPERVISEUR);
            seed("agent@fichedevoyage.td",       "Agent@2026",  "HASSAN",   "Abdelkerim Agent",  RoleUser.AGENT_FRONTIERE);
        };
    }

    private void seed(String email, String password, String nom, String prenoms, RoleUser role) {
        if (userRepo.findByEmail(email).isEmpty()) {
            userRepo.save(User.builder()
                    .email(email)
                    .passwordHash(passwordEncoder.encode(password))
                    .nom(nom)
                    .prenoms(prenoms)
                    .role(role)
                    .active(true)
                    .build());
            log.info("[DEV] Utilisateur créé : {} ({})", email, role);
        }
    }
}
