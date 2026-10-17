package td.gov.fichedevoyage.domain.port;

import td.gov.fichedevoyage.domain.model.FicheVoyage;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface FicheVoyageRepository {
    FicheVoyage save(FicheVoyage fiche);
    Optional<FicheVoyage> findById(UUID id);
    Optional<FicheVoyage> findByQrCodeToken(String token);
    Optional<FicheVoyage> findByReference(String reference);
    Optional<FicheVoyage> findByNumeroDocumentAndDateDelivranceAndDateVoyage(
            String numeroDocument, LocalDate dateDelivrance, LocalDate dateVoyage);
    String generateNextReference();
}
