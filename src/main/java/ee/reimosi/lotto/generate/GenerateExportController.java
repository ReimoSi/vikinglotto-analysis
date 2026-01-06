package ee.reimosi.lotto.generate;

import ee.reimosi.lotto.generate.dto.GeneratedTicket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
@Tag(name = "generator", description = "Vikinglotto ticket generator")
public class GenerateExportController {

    private final NumberGeneratorService service;

    @Operation(summary = "Export generated tickets as CSV",
            description = "Columns: main1,main2,main3,main4,main5,main6,bonus")
    @GetMapping(value = "/export.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(defaultValue = "uniform") String method,
            @RequestParam(defaultValue = "1") int rows,
            @RequestParam(required = false) Long seed
    ) {
        if (rows < 1) rows = 1;
        if (rows > 200) rows = 200;

        var m = switch (method.toLowerCase()) {
            case "anti-popular", "antipopular", "anti" -> NumberGeneratorService.Method.ANTI_POPULAR;
            default -> NumberGeneratorService.Method.UNIFORM;
        };

        List<GeneratedTicket> tickets = service.generate(m, rows, seed);

        StringBuilder sb = new StringBuilder();
        sb.append("main1,main2,main3,main4,main5,main6,bonus\n");
        for (GeneratedTicket t : tickets) {
            var main = t.getMain();
            sb.append(quote(String.valueOf(main.get(0)))).append(',')
                    .append(quote(String.valueOf(main.get(1)))).append(',')
                    .append(quote(String.valueOf(main.get(2)))).append(',')
                    .append(quote(String.valueOf(main.get(3)))).append(',')
                    .append(quote(String.valueOf(main.get(4)))).append(',')
                    .append(quote(String.valueOf(main.get(5)))).append(',')
                    .append(quote(String.valueOf(t.getBonus()))).append('\n');
        }

        // UTF-8 BOM, et Excel loeks täpitähed/jutumärgid korrektselt
        byte[] bom  = new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF};
        byte[] data = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] out  = new byte[bom.length + data.length];
        System.arraycopy(bom, 0, out, 0, bom.length);
        System.arraycopy(data, 0, out, bom.length, data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"generated-tickets.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(out);
    }

    private static String quote(String v) {
        if (v == null) return "\"\"";
        return "\"" + v.replace("\"", "\"\"") + "\"";
    }

}
