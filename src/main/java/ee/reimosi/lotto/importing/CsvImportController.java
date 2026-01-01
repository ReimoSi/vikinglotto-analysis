package ee.reimosi.lotto.importing;

import ee.reimosi.lotto.importing.dto.CsvImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/import")
@RequiredArgsConstructor
@Tag(name = "admin-import", description = "Admin CSV import for draws")
public class CsvImportController {

    private final CsvImportService service;

    @Operation(summary = "Import draws from CSV (ADMIN)",
            description = """
        CSV columns: draw_id,draw_date,main_numbers,bonus_numbers
        Date format: ISO (YYYY-MM-DD)
        First non-empty line may be header or data row.
        Duplicates (by draw_id) are skipped.
        """)
    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CsvImportResult importCsv(@RequestPart("file") MultipartFile file) {
        return service.importCsv(file);
    }
}
