package ee.reimosi.lotto.draw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DrawRequest {
    @NotBlank private String drawId;
    @NotNull  private LocalDate drawDate;
    @NotBlank private String mainNumbers;
    @NotBlank private String bonusNumbers;
}
