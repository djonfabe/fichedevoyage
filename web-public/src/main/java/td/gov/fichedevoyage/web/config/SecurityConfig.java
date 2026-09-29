package td.gov.fichedevoyage.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/home", "/P0013", "/P0013/modifier/**", "/searchFicheInfo",
                    "/contact", "/faq", "/lang/**",
                    "/v/**",
                    "/fiche/**",
                    "/api/v1/fiches/**",
                    "/api/v1/pays", "/api/v1/pays/**",
                    "/api/v1/compagnies", "/api/v1/compagnies/**",
                    "/api/v1/vols", "/api/v1/vols/**",
                    "/css/**", "/js/**", "/img/**", "/favicon.ico",
                    "/h2-console/**",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
