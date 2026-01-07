package ee.reimosi.lotto.importing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvImportResult {
    private int imported;
    private int skipped;
    private int errors;
}
