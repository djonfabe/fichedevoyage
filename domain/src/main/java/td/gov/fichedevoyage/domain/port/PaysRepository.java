package td.gov.fichedevoyage.domain.port;

import td.gov.fichedevoyage.domain.model.Pays;
import java.util.List;
import java.util.Optional;

public interface PaysRepository {
    List<Pays> findAllOrderByNomFr();
    List<Pays> findAllOrderByNomEn();
    Optional<Pays> findById(Long id);
    Optional<Pays> findByCodeIso2(String code);
}
