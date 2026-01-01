package ee.reimosi.lotto.generate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "A single Vikinglotto ticket")
public class GeneratedTicket {
    @Schema(description = "6 unique main numbers (1..48)", example = "[4, 6, 9, 23, 38, 47]")
    private List<Integer> main;

    @Schema(description = "Bonus number (1..5)", example = "4")
    private int bonus;

    public GeneratedTicket() {}
    public GeneratedTicket(List<Integer> main, int bonus) { this.main = main; this.bonus = bonus; }

    public List<Integer> getMain() { return main; }
    public void setMain(List<Integer> main) { this.main = main; }
    public int getBonus() { return bonus; }
    public void setBonus(int bonus) { this.bonus = bonus; }
}
