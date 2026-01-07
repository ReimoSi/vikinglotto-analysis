package ee.reimosi.lotto.analysis;

import ee.reimosi.lotto.analysis.dto.AnalysisSummary;
import ee.reimosi.lotto.draw.Draw;
import ee.reimosi.lotto.draw.DrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalysisService {

    private final DrawRepository repo;

    // Vikinglotto rules
    private static final int MAIN_POOL = 48;
    private static final int MAIN_PER_DRAW = 6;
    private static final int BONUS_POOL = 5; // informatiivne; bonus freq arvestame 1..5

    public AnalysisSummary summary() {
        var draws = repo.findAll();
        int drawCount = draws.size();

        int[] mainFreq = new int[MAIN_POOL + 1];  // 1..48
        int[] bonusFreq = new int[BONUS_POOL + 1]; // 1..5

        for (Draw d : draws) {
            for (int n : parseNums(d.getMainNumbers())) {
                if (n >= 1 && n <= MAIN_POOL) mainFreq[n]++;
            }
            for (int b : parseNums(d.getBonusNumbers())) {
                if (b >= 1 && b <= BONUS_POOL) bonusFreq[b]++;
            }
        }

        long totalMainPicks = (long) drawCount * MAIN_PER_DRAW;
        double expected = totalMainPicks / (double) MAIN_POOL;

        double chi2 = 0.0;
        for (int i = 1; i <= MAIN_POOL; i++) {
            double diff = mainFreq[i] - expected;
            chi2 += (diff * diff) / expected;
        }
        int df = MAIN_POOL - 1;

        var dto = new AnalysisSummary();
        dto.setDrawCount(drawCount);
        dto.setMainPoolSize(MAIN_POOL);
        dto.setMainPerDraw(MAIN_PER_DRAW);
        dto.setTotalMainPicks(totalMainPicks);
        dto.setChiSquareStat(chi2);
        dto.setChiSquareDf(df);
        dto.setMainFrequencies(toCounts(mainFreq, 1, MAIN_POOL));
        dto.setBonusFrequencies(toCounts(bonusFreq, 1, BONUS_POOL));
        return dto;
    }

    private static List<Integer> parseNums(String s) {
        var arr = new ArrayList<Integer>();
        if (s == null || s.isBlank()) return arr;
        for (String p : s.trim().split("\\s+")) {
            try {
                arr.add(Integer.parseInt(p));
            } catch (NumberFormatException ignored) {
            }
        }
        return arr;
    }

    private static List<AnalysisSummary.Count> toCounts(int[] freq, int from, int to) {
        var out = new ArrayList<AnalysisSummary.Count>(to - from + 1);
        for (int i = from; i <= to; i++) out.add(new AnalysisSummary.Count(i, freq[i]));
        return out;
    }
}

