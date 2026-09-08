package td.gov.fichedevoyage.agent.controller;

import td.gov.fichedevoyage.agent.security.JwtService;
import td.gov.fichedevoyage.domain.model.User;
import td.gov.fichedevoyage.domain.port.UserRepository;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agent/auth")
@RequiredArgsConstructor
public class AuthAgentController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return userRepo.findByEmail(req.getEmail())
                .filter(u -> u.isActive()
                        && u.getRole() == td.gov.fichedevoyage.domain.enums.RoleUser.AGENT_FRONTIERE
                        && passwordEncoder.matches(req.getPassword(), u.getPasswordHash()))
                .map(u -> {
                    u.setLastLoginAt(LocalDateTime.now());
                    userRepo.save(u);
                    String token = jwtService.generateToken(u.getEmail(),
                            Map.of("role", u.getRole().name(), "userId", u.getId()));
                    return ResponseEntity.ok(Map.of(
                            "accessToken", token,
                            "tokenType", "Bearer",
                            "nom", u.getNom() + " " + u.getPrenoms(),
                            "role", u.getRole().name()
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Identifiants invalides ou rôle insuffisant")));
    }

    @Data
    static class LoginRequest {
        private String email;
        private String password;
    }
}
