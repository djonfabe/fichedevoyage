package td.gov.fichedevoyage.infrastructure.pdf;

import td.gov.fichedevoyage.application.dto.FicheResponse;
import td.gov.fichedevoyage.application.service.PdfGenerationService;
import td.gov.fichedevoyage.application.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class FlyingSaucerPdfAdapter implements PdfGenerationService {

    private final TemplateEngine templateEngine;
    private final QrCodeService qrCodeService;

    @Value("${app.base-url:https://fichedevoyage.gov.td}")
    private String baseUrl;

    // Cache the Base64 representation of armoirie.svg (220KB) after initial load.
    // Prevents reading from classpath InputStream and re-encoding Base64 on every PDF generation.
    private volatile String cachedLogoBase64;

    private String loadLogoBase64() {
        if (cachedLogoBase64 == null) {
            synchronized (this) {
                if (cachedLogoBase64 == null) {
                    try (InputStream in = new ClassPathResource("static/img/armoirie.svg").getInputStream()) {
                        cachedLogoBase64 = "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(in.readAllBytes());
                    } catch (IOException e) {
                        cachedLogoBase64 = "";
                    }
                }
            }
        }
        return cachedLogoBase64;
    }

    @Override
    public byte[] generateFichePdf(FicheResponse fiche) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            writeFichePdf(fiche, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur génération PDF", e);
        }
    }

    @Override
    public void writeFichePdf(FicheResponse fiche, OutputStream out) {
        try {
            String qrContent = baseUrl + "/v/" + fiche.getQrCodeToken();
            byte[] qrBytes = qrCodeService.generatePng(qrContent, 200, 200);
            String qrBase64 = Base64.getEncoder().encodeToString(qrBytes);

            Context ctx = new Context();
            ctx.setVariable("fiche", fiche);
            ctx.setVariable("qrBase64", qrBase64);
            ctx.setVariable("baseUrl", baseUrl);
            ctx.setVariable("logoBase64", loadLogoBase64());

            String html = templateEngine.process("pdf/fiche-pdf", ctx);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(out);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF fiche voyage", e);
        }
    }
}
