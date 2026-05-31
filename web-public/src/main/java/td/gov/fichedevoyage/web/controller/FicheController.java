package td.gov.fichedevoyage.web.controller;

import td.gov.fichedevoyage.application.dto.*;
import td.gov.fichedevoyage.application.service.FicheVoyageService;
import td.gov.fichedevoyage.domain.enums.StatutFiche;
import td.gov.fichedevoyage.application.service.PdfGenerationService;
import td.gov.fichedevoyage.application.service.QrCodeService;
import td.gov.fichedevoyage.domain.port.CompagnieRepository;
import td.gov.fichedevoyage.domain.port.PaysRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class FicheController {

    private final FicheVoyageService ficheService;
    private final PdfGenerationService pdfService;
    private final QrCodeService qrService;
    private final PaysRepository paysRepo;
    private final CompagnieRepository compagnieRepo;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/P0013")
    public String formulaire(Model model) {
        model.addAttribute("ficheRequest", new FicheCreationRequest());
        model.addAttribute("pays", paysRepo.findAllOrderByNomFr());
        model.addAttribute("compagnies", compagnieRepo.findAllActive());
        return "fiche/wizard";
    }

    @PostMapping("/P0013")
    public String soumettreFormulaire(
            @Valid @ModelAttribute("ficheRequest") FicheCreationRequest request,
            BindingResult result,
            Model model,
            HttpServletRequest httpRequest) {

        if (result.hasErrors()) {
            model.addAttribute("pays", paysRepo.findAllOrderByNomFr());
            model.addAttribute("compagnies", compagnieRepo.findAllActive());
            return "fiche/wizard";
        }

        String ip = getClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        FicheResponse fiche = ficheService.creerFiche(request, ip, ua);

        return "redirect:/fiche/succes/" + fiche.getQrCodeToken();
    }

    @GetMapping("/fiche/succes/{token}")
    public String succes(@PathVariable String token, Model model) {
        return ficheService.getByToken(token)
                .map(fiche -> {
                    model.addAttribute("fiche", fiche);
                    model.addAttribute("qrUrl", baseUrl + "/v/" + token);
                    return "fiche/succes";
                })
                .orElse("redirect:/");
    }

    @GetMapping("/v/{token}")
    public String verifierFiche(@PathVariable String token, Model model) {
        return ficheService.getByToken(token)
                .map(fiche -> {
                    model.addAttribute("fiche", fiche);
                    return "fiche/verification";
                })
                .orElse("error/404");
    }

    @GetMapping("/searchFicheInfo")
    public String rechercheForm(Model model) {
        model.addAttribute("searchRequest", new FicheSearchRequest());
        return "fiche/recherche";
    }

    @PostMapping("/searchFicheInfo")
    public String rechercheSubmit(
            @Valid @ModelAttribute("searchRequest") FicheSearchRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "fiche/recherche";
        }

        return ficheService.rechercherFiche(request)
                .map(fiche -> {
                    model.addAttribute("fiche", fiche);
                    return "fiche/modifier";
                })
                .orElseGet(() -> {
                    model.addAttribute("notFound", true);
                    return "fiche/recherche";
                });
    }

    @GetMapping("/P0013/modifier/{token}")
    public String modifierForm(@PathVariable String token, Model model) {
        return ficheService.getByToken(token)
                .filter(f -> f.getStatut() == StatutFiche.VALIDEE)
                .map(fiche -> {
                    model.addAttribute("ficheRequest", toCreationRequest(fiche));
                    model.addAttribute("pays", paysRepo.findAllOrderByNomFr());
                    model.addAttribute("compagnies", compagnieRepo.findAllActive());
                    model.addAttribute("modificationToken", token);
                    return "fiche/wizard";
                })
                .orElse("error/404");
    }

    @PostMapping("/P0013/modifier/{token}")
    public String soumettreModification(
            @PathVariable String token,
            @Valid @ModelAttribute("ficheRequest") FicheCreationRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("pays", paysRepo.findAllOrderByNomFr());
            model.addAttribute("compagnies", compagnieRepo.findAllActive());
            model.addAttribute("modificationToken", token);
            return "fiche/wizard";
        }

        FicheResponse updated = ficheService.modifierFiche(token, request);
        return "redirect:/fiche/succes/" + updated.getQrCodeToken();
    }

    private FicheCreationRequest toCreationRequest(FicheResponse f) {
        return FicheCreationRequest.builder()
                .nom(f.getNom())
                .prenoms(f.getPrenoms())
                .nomJeuneFille(f.getNomJeuneFille())
                .sexe(f.getSexe())
                .dateNaissance(f.getDateNaissance())
                .lieuNaissance(f.getLieuNaissance())
                .email(f.getEmail())
                .profession(f.getProfession())
                .fonction(f.getFonction())
                .nationaliteId(f.getNationaliteId())
                .paysResidenceId(f.getPaysResidenceId())
                .typeDocument(f.getTypeDocument())
                .numeroDocument(f.getNumeroDocument())
                .dateDelivrance(f.getDateDelivrance())
                .lieuDelivrance(f.getLieuDelivrance())
                .paysProvenanceId(f.getPaysProvenanceId())
                .villeProvenance(f.getVilleProvenance())
                .paysDestinationId(f.getPaysDestinationId())
                .villeDestination(f.getVilleDestination())
                .dateVoyage(f.getDateVoyage())
                .motifVoyage(f.getMotifVoyage())
                .dureeSejour(f.getDureeSejour())
                .typeVoyage(f.getTypeVoyage())
                .typeHebergement(f.getTypeHebergement())
                .compagnieId(f.getCompagnieId())
                .numeroVol(f.getNumeroVol())
                .engagementAccepte(Boolean.TRUE)
                .build();
    }

    @PostMapping(value = "/P0013", params = "download")
    public Object soumettreEtTelecharger(
            @Valid @ModelAttribute("ficheRequest") FicheCreationRequest request,
            BindingResult result,
            Model model,
            HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            model.addAttribute("pays", paysRepo.findAllOrderByNomFr());
            model.addAttribute("compagnies", compagnieRepo.findAllActive());
            return "fiche/wizard";
        }
        String ip = getClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        FicheResponse fiche = ficheService.creerFiche(request, ip, ua);
        byte[] pdf = pdfService.generateFichePdf(fiche);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"fiche-" + fiche.getReference() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/api/v1/fiches/{token}/pdf")
    public ResponseEntity<byte[]> telechargerPdf(@PathVariable String token) {
        return ficheService.getByToken(token)
                .map(fiche -> {
                    byte[] pdf = pdfService.generateFichePdf(fiche);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"fiche-" + fiche.getReference() + ".pdf\"")
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(pdf);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/v1/fiches/{token}/qr")
    public ResponseEntity<byte[]> qrCode(@PathVariable String token) {
        String qrContent = baseUrl + "/v/" + token;
        byte[] png = qrService.generatePng(qrContent, 300, 300);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
