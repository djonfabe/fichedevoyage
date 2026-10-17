package td.gov.fichedevoyage.application.service;

import td.gov.fichedevoyage.application.dto.FicheResponse;

public interface EmailService {
    void envoyerConfirmation(FicheResponse fiche);
    void envoyerFicheParEmail(FicheResponse fiche, String destinataire);
}
