package ee.reimosi.lotto.draw.dto;

import lombok.Value;
import java.time.LocalDate;

@Value
public class DrawResponse {
    Long id;
    String drawId;
    LocalDate drawDate;
    String mainNumbers;
    String bonusNumbers;
}

