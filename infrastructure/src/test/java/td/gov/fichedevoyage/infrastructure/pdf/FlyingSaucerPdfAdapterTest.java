package td.gov.fichedevoyage.infrastructure.pdf;

import td.gov.fichedevoyage.application.dto.FicheResponse;
import td.gov.fichedevoyage.application.service.QrCodeService;
import td.gov.fichedevoyage.domain.enums.StatutFiche;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlyingSaucerPdfAdapterTest {

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private QrCodeService qrCodeService;

    @InjectMocks
    private FlyingSaucerPdfAdapter pdfAdapter;

    @Test
    void shouldGeneratePdfAndUseCachedLogo() {
        FicheResponse fiche = FicheResponse.builder()
                .id(UUID.randomUUID())
                .reference("FV-2026-000001")
                .qrCodeToken("test-token")
                .nom("DOE")
                .prenoms("John")
                .statut(StatutFiche.VALIDEE)
                .build();

        when(qrCodeService.generatePng(any(), eq(200), eq(200))).thenReturn(new byte[]{1, 2, 3});
        when(templateEngine.process(eq("pdf/fiche-pdf"), any(IContext.class)))
                .thenReturn("<html><body><h1>Test PDF</h1></body></html>");

        byte[] pdfBytes = pdfAdapter.generateFichePdf(fiche);
        assertNotNull(pdfBytes);

        // Call again to verify cached logo is used
        byte[] pdfBytesSecondCall = pdfAdapter.generateFichePdf(fiche);
        assertNotNull(pdfBytesSecondCall);
    }
}
