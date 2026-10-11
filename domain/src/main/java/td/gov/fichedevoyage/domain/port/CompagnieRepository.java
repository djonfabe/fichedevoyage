package td.gov.fichedevoyage.domain.port;

import td.gov.fichedevoyage.domain.model.Compagnie;
import java.util.List;
import java.util.Optional;

public interface CompagnieRepository {
    List<Compagnie> findAllActive();
    Optional<Compagnie> findById(Long id);
    Compagnie save(Compagnie compagnie);
    void deleteById(Long id);
}
