package td.gov.fichedevoyage.admin.controller;

import td.gov.fichedevoyage.admin.dto.LoginRequest;
import td.gov.fichedevoyage.admin.dto.TokenResponse;
import td.gov.fichedevoyage.admin.security.JwtService;
import td.gov.fichedevoyage.domain.model.User;
import td.gov.fichedevoyage.domain.port.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AuthAdminController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 30;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElse(null);

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Identifiants invalides"));
        }

        // Vérification verrouillage
        if (user.getVerrouilleJusquAu() != null
                && LocalDateTime.now().isBefore(user.getVerrouilleJusquAu())) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(Map.of("message", "Compte verrouillé. Réessayez après " + user.getVerrouilleJusquAu()));
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            int attempts = user.getTentativesEchecs() + 1;
            user.setTentativesEchecs(attempts);
            if (attempts >= MAX_ATTEMPTS) {
                user.setVerrouilleJusquAu(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                user.setTentativesEchecs(0);
            }
            userRepo.save(user);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Identifiants invalides"));
        }

        // TOTP requis ?
        if (user.isTotpEnabled()) {
            if (req.getTotpCode() == null || req.getTotpCode().isBlank()) {
                return ResponseEntity.ok(TokenResponse.builder()
                        .totpRequired(true)
                        .tokenType("Bearer")
                        .nom(user.getNom())
                        .build());
            }
            // TODO: vérifier le code TOTP avec la bibliothèque Google Authenticator
            // En attendant, rejeter toute tentative pour éviter le contournement
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "La vérification TOTP n'est pas encore activée"));
        }

        // Succès
        user.setTentativesEchecs(0);
        user.setVerrouilleJusquAu(null);
        user.setLastLoginAt(LocalDateTime.now());
        userRepo.save(user);

        String accessToken = jwtService.generateToken(user.getEmail(),
                Map.of("role", user.getRole().name(), "userId", user.getId()));
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900)
                .role(user.getRole().name())
                .nom(user.getNom() + " " + user.getPrenoms())
                .totpRequired(false)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token invalide ou expiré"));
        }

        String email = jwtService.extractSubject(refreshToken);
        return userRepo.findByEmail(email)
                .filter(User::isActive)
                .map(user -> {
                    String newAccess = jwtService.generateToken(email,
                            Map.of("role", user.getRole().name(), "userId", user.getId()));
                    String newRefresh = jwtService.generateRefreshToken(email);
                    return ResponseEntity.ok(TokenResponse.builder()
                            .accessToken(newAccess)
                            .refreshToken(newRefresh)
                            .tokenType("Bearer")
                            .expiresIn(900)
                            .build());
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
