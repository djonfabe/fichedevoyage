package td.gov.fichedevoyage.infrastructure.persistence.jpa;

import td.gov.fichedevoyage.domain.enums.StatutFiche;
import td.gov.fichedevoyage.domain.model.FicheVoyage;
import td.gov.fichedevoyage.domain.port.FicheVoyageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FicheVoyageJpaRepository
        extends JpaRepository<FicheVoyage, UUID>, FicheVoyageRepository {

    @Query("SELECT f FROM FicheVoyage f WHERE f.id = :id")
    Optional<FicheVoyage> findFicheById(@Param("id") UUID id);

    Optional<FicheVoyage> findByQrCodeToken(String token);
    Optional<FicheVoyage> findByReference(String reference);
    Optional<FicheVoyage> findByNumeroDocumentAndDateDelivranceAndDateVoyage(
            String numeroDocument, LocalDate dateDelivrance, LocalDate dateVoyage);

    long countByDateVoyage(LocalDate dateVoyage);
    long countByDateVoyageBetween(LocalDate start, LocalDate end);
    long countByStatutAndDateVoyage(StatutFiche statut, LocalDate dateVoyage);

    Page<FicheVoyage> findByDateVoyageBetween(LocalDate start, LocalDate end, Pageable pageable);

    Page<FicheVoyage> findByStatut(StatutFiche statut, Pageable pageable);

    // Optimized count query by status without fetching page contents
    long countByStatut(StatutFiche statut);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(f.reference, 9) AS int)), 0) + 1 " +
           "FROM FicheVoyage f WHERE f.reference LIKE CONCAT('FV-', :annee, '-%')")
    int getNextSequence(@Param("annee") int annee);

    @Query("SELECT FUNCTION('MONTH', f.dateVoyage) as mois, COUNT(f) as total " +
           "FROM FicheVoyage f " +
           "WHERE FUNCTION('YEAR', f.dateVoyage) = :year " +
           "GROUP BY FUNCTION('MONTH', f.dateVoyage) " +
           "ORDER BY mois")
    List<Object[]> countByMonth(@Param("year") int year);

    default String generateNextReference() {
        int annee = Year.now().getValue();
        int seq = getNextSequence(annee);
        return String.format("FV-%d-%06d", annee, seq);
    }
}
