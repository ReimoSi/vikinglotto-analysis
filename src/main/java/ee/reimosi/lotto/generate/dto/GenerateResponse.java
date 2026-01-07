package ee.reimosi.lotto.generate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "Generator response")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateResponse {

    @Schema(description = "Selected method", example = "uniform")
    private String method;

    @Schema(description = "How many rows were generated", example = "5")
    private int rows;

    @Schema(description = "Generated tickets")
    private List<GeneratedTicket> tickets;
}
