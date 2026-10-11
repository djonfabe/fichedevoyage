package td.gov.fichedevoyage.infrastructure.persistence.jpa;

import td.gov.fichedevoyage.domain.enums.PositionPublicite;
import td.gov.fichedevoyage.domain.model.Publicite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PubliciteJpaRepository extends JpaRepository<Publicite, Long> {

    @Query("SELECT p FROM Publicite p WHERE p.active = true AND p.position = :pos " +
           "AND (p.dateDebut IS NULL OR p.dateDebut <= :today) " +
           "AND (p.dateFin IS NULL OR p.dateFin >= :today)")
    List<Publicite> findActiveByPosition(@Param("pos") PositionPublicite position,
                                         @Param("today") LocalDate today);
}
