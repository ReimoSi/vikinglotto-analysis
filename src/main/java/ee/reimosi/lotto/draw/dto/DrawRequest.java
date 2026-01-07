package ee.reimosi.lotto.draw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrawRequest {
    @NotBlank
    private String drawId;
    @NotNull
    private LocalDate drawDate;
    @NotBlank
    private String mainNumbers;
    @NotBlank
    private String bonusNumbers;
}
