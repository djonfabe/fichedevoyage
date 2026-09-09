package td.gov.fichedevoyage.web.controller;

import td.gov.fichedevoyage.domain.model.Publicite;
import td.gov.fichedevoyage.domain.enums.PositionPublicite;
import td.gov.fichedevoyage.infrastructure.persistence.jpa.PubliciteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PubliciteJpaRepository publiciteRepo;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Publicite> slides = publiciteRepo.findActiveByPosition(PositionPublicite.ACCUEIL, LocalDate.now());
        model.addAttribute("slides", slides);
        return "home";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/lang/{code}")
    public String changeLanguage(@PathVariable String code) {
        return "redirect:/";
    }
}
