package td.gov.fichedevoyage.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class FicheSearchRequest {

    @NotBlank
    private String numeroDocument;

    @NotNull
    private LocalDate dateDelivrance;

    @NotNull
    private LocalDate dateVoyage;
}
