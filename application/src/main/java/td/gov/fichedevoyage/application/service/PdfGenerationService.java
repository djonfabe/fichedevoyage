package td.gov.fichedevoyage.application.service;

import td.gov.fichedevoyage.application.dto.FicheResponse;
import java.io.OutputStream;

public interface PdfGenerationService {
    byte[] generateFichePdf(FicheResponse fiche);
    void writeFichePdf(FicheResponse fiche, OutputStream out);
}
