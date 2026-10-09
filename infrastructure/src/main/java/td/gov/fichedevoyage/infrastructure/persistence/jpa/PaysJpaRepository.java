package td.gov.fichedevoyage.infrastructure.persistence.jpa;

import td.gov.fichedevoyage.domain.model.Pays;
import td.gov.fichedevoyage.domain.port.PaysRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaysJpaRepository extends JpaRepository<Pays, Long>, PaysRepository {

    List<Pays> findAllByOrderByNomFrAsc();
    List<Pays> findAllByOrderByNomEnAsc();
    Optional<Pays> findByCodeIso2(String code);

    default List<Pays> findAllOrderByNomFr() { return findAllByOrderByNomFrAsc(); }
    default List<Pays> findAllOrderByNomEn() { return findAllByOrderByNomEnAsc(); }
}
