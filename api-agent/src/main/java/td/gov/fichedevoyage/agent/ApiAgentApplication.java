package td.gov.fichedevoyage.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = "td.gov.fichedevoyage")
@EntityScan(basePackages = "td.gov.fichedevoyage.domain.model")
@EnableJpaRepositories(basePackages = "td.gov.fichedevoyage.infrastructure.persistence.jpa")
public class ApiAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiAgentApplication.class, args);
    }
}
