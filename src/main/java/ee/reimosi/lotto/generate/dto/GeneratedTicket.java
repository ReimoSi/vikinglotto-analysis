package ee.reimosi.lotto.generate.dto;

import java.util.List;

public class GeneratedTicket {
    private List<Integer> main; // 6 unique in 1..48
    private int bonus;          // 1..5

    public GeneratedTicket() {}
    public GeneratedTicket(List<Integer> main, int bonus) {
        this.main = main; this.bonus = bonus;
    }

    public List<Integer> getMain() { return main; }
    public void setMain(List<Integer> main) { this.main = main; }
    public int getBonus() { return bonus; }
    public void setBonus(int bonus) { this.bonus = bonus; }
}
