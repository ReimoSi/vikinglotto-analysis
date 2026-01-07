package ee.reimosi.lotto.importing;

import ee.reimosi.lotto.draw.Draw;
import ee.reimosi.lotto.draw.DrawRepository;
import ee.reimosi.lotto.importing.dto.CsvImportResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class CsvImportService {

    private final DrawRepository repo;

    public CsvImportResult importCsv(MultipartFile file) {
        int imported = 0, skipped = 0, errors = 0;

        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean headerChecked = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Header kontroll (esimene mittetühi rida)
                if (!headerChecked) {
                    headerChecked = true;
                    if (!isHeader(line)) {
                        // Kui pole headerit, käsitle nagu andmerida
                        var ok = persistLine(line);
                        if (ok == 1) imported++;
                        else if (ok == 0) skipped++;
                        else errors++;
                    }
                    continue;
                }

                int ok = persistLine(line);
                if (ok == 1) imported++;
                else if (ok == 0) skipped++;
                else errors++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV: " + e.getMessage(), e);
        }

        return new CsvImportResult(imported, skipped, errors);
    }

    private boolean isHeader(String line) {
        String norm = line.toLowerCase();
        return norm.contains("draw_id") && norm.contains("draw_date")
                && norm.contains("main_numbers") && norm.contains("bonus_numbers");
    }

    /**
     * @return 1 imported, 0 skipped (duplicate), -1 error
     */
    private int persistLine(String line) {
        // assume, that those fields do not include commas
        // format: draw_id,draw_date,main_numbers,bonus_numbers
        String[] parts = line.split(",", -1);
        if (parts.length < 4) return -1;

        String drawId = parts[0].trim();
        String dateStr = parts[1].trim();
        String main = parts[2].trim();
        String bonus = parts[3].trim();

        if (drawId.isEmpty() || dateStr.isEmpty() || main.isEmpty() || bonus.isEmpty()) return -1;

        if (repo.existsByDrawId(drawId)) return 0;

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception ex) {
            return -1;
        }

        var e = Draw.builder()
                .drawId(drawId)
                .drawDate(date)
                .mainNumbers(main)
                .bonusNumbers(bonus)
                .build();
        repo.save(e);
        return 1;
    }
}
