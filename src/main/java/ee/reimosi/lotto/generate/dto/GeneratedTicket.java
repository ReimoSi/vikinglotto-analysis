package ee.reimosi.lotto.generate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "A single Vikinglotto ticket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedTicket {

    @Schema(description = "6 unique main numbers (1..48)", example = "[4, 6, 9, 23, 38, 47]")
    private List<Integer> main;

    @Schema(description = "Bonus number (1..5)", example = "4")
    private int bonus;
}
