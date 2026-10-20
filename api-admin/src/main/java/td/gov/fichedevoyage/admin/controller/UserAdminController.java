package td.gov.fichedevoyage.admin.controller;

import td.gov.fichedevoyage.domain.model.User;
import td.gov.fichedevoyage.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> list() { return userRepo.findAll(); }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role");
        if (email == null || password == null || role == null) {
            return ResponseEntity.badRequest().build();
        }
        td.gov.fichedevoyage.domain.enums.RoleUser roleEnum;
        try {
            roleEnum = td.gov.fichedevoyage.domain.enums.RoleUser.valueOf(role);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .nom(body.get("nom"))
                .prenoms(body.get("prenoms"))
                .role(roleEnum)
                .active(true)
                .build();
        return ResponseEntity.ok(userRepo.save(user));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id,
                                              @RequestBody Map<String, Boolean> body) {
        userRepo.findById(id).ifPresent(u -> {
            u.setActive(body.getOrDefault("active", true));
            userRepo.save(u);
        });
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
