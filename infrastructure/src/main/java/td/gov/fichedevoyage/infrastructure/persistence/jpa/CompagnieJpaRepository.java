package td.gov.fichedevoyage.infrastructure.persistence.jpa;

import td.gov.fichedevoyage.domain.model.Compagnie;
import td.gov.fichedevoyage.domain.port.CompagnieRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompagnieJpaRepository extends JpaRepository<Compagnie, Long>, CompagnieRepository {

    List<Compagnie> findByActiveTrueOrderByNomAsc();

    default List<Compagnie> findAllActive() { return findByActiveTrueOrderByNomAsc(); }
}
