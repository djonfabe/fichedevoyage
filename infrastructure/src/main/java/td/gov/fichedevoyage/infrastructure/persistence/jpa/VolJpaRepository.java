package td.gov.fichedevoyage.infrastructure.persistence.jpa;

import td.gov.fichedevoyage.domain.model.Vol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolJpaRepository extends JpaRepository<Vol, Long> {
    List<Vol> findByCompagnieIdAndActiveTrue(Long compagnieId);
    List<Vol> findByActiveTrueOrderByNumeroAsc();
}
