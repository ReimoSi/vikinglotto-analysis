package ee.reimosi.lotto.generate;

import ee.reimosi.lotto.generate.dto.GeneratedTicket;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NumberGeneratorService {

    private static final int MAIN_POOL = 48;
    private static final int BONUS_POOL = 5;
    private static final int MAIN_PER_DRAW = 6;

    public enum Method { UNIFORM, ANTI_POPULAR }

    public List<GeneratedTicket> generate(Method method, int rows, Long seed) {
        Random rnd = (seed == null) ? new SecureRandom() : new Random(seed);
        List<GeneratedTicket> out = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            List<Integer> main;
            int bonus;
            int guard = 0;
            do {
                main = uniformMain(rnd);
                bonus = 1 + rnd.nextInt(BONUS_POOL);
                guard++;
                if (method == Method.UNIFORM) break;
            } while (method == Method.ANTI_POPULAR && !isAcceptable(main) && guard < 500);

            Collections.sort(main);
            out.add(new GeneratedTicket(main, bonus));
        }
        return out;
    }

    private List<Integer> uniformMain(Random rnd) {
        // Fisher–Yates: vali 6 unikaalset 1..48
        List<Integer> pool = new ArrayList<>(MAIN_POOL);
        for (int n = 1; n <= MAIN_POOL; n++) pool.add(n);
        Collections.shuffle(pool, rnd);
        return pool.subList(0, MAIN_PER_DRAW).stream().sorted().collect(Collectors.toList());
    }

    // Heuristikad, mis väldivad "rahvamustreid"
    private boolean isAcceptable(List<Integer> main) {
        // 1) kalendri-ülekaal: mitte rohkem kui 4 numbrit 1..31
        long calendar = main.stream().filter(n -> n <= 31).count();
        if (calendar > 4) return false;

        // 2) sama lõpunumber: mitte >=4 sama viimase numbriga
        int[] lastDigit = new int[10];
        for (int n : main) lastDigit[n % 10]++;
        for (int c : lastDigit) if (c >= 4) return false;

        // 3) aritmilised jadad (nt 3,8,13,18,23,28) – kontrolli kui kõigil vahe sama
        List<Integer> diffs = new ArrayList<>(main.size() - 1);
        for (int i = 1; i < main.size(); i++) diffs.add(main.get(i) - main.get(i - 1));
        boolean arithmetic = diffs.stream().distinct().count() == 1;
        if (arithmetic) return false;

        // 4) liiga klompis (summaarne standardhälve väga väike) – kerge filter
        double mean = main.stream().mapToInt(Integer::intValue).average().orElse(0);
        double var = main.stream().mapToDouble(n -> (n - mean) * (n - mean)).sum() / main.size();
        double sd = Math.sqrt(var);
        if (sd < 12.0) return false;

        return true;
    }
}

