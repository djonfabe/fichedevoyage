package td.gov.fichedevoyage.infrastructure.mail;

import td.gov.fichedevoyage.application.dto.FicheResponse;
import td.gov.fichedevoyage.application.service.EmailService;
import td.gov.fichedevoyage.application.service.PdfGenerationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringMailAdapter implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final PdfGenerationService pdfService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void envoyerConfirmation(FicheResponse fiche) {
        if (fiche.getEmail() == null || fiche.getEmail().isBlank()) return;
        try {
            Context ctx = new Context();
            ctx.setVariable("fiche", fiche);
            String html = templateEngine.process("email/confirmation", ctx);
            sendHtmlEmail(fiche.getEmail(),
                    "Confirmation de votre fiche de voyage — " + fiche.getReference(),
                    html);
        } catch (Exception e) {
            log.error("Erreur envoi email confirmation fiche {}", fiche.getReference(), e);
        }
    }

    @Async
    @Override
    public void envoyerFicheParEmail(FicheResponse fiche, String destinataire) {
        try {
            byte[] pdf = pdfService.generateFichePdf(fiche);
            Context ctx = new Context();
            ctx.setVariable("fiche", fiche);
            String html = templateEngine.process("email/fiche-attachee", ctx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(destinataire);
            helper.setSubject("Votre fiche de voyage — " + fiche.getReference());
            helper.setText(html, true);
            helper.addAttachment("fiche-voyage-" + fiche.getReference() + ".pdf",
                    () -> new java.io.ByteArrayInputStream(pdf),
                    "application/pdf");
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Erreur envoi fiche par email {}", fiche.getReference(), e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }
}
