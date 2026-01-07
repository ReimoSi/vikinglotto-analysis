package ee.reimosi.lotto.draw;

import ee.reimosi.lotto.draw.dto.DrawRequest;
import ee.reimosi.lotto.draw.dto.DrawResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/draws")
@RequiredArgsConstructor
@Tag(name = "draws", description = "CRUD for Vikinglotto draws")
public class DrawController {
    private final DrawService service;

    @Operation(summary = "List all draws")
    @GetMapping
    public List<DrawResponse> list() {
        return service.list();
    }

    @Operation(summary = "Get a draw by ID")
    @GetMapping("/{id}")
    public DrawResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Create a new draw")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DrawResponse create(@Valid @RequestBody DrawRequest req) {
        return service.create(req);
    }

    @Operation(summary = "Update an existing draw")
    @PutMapping("/{id}")
    public DrawResponse update(@PathVariable Long id, @Valid @RequestBody DrawRequest req) {
        return service.update(id, req);
    }

    @Operation(summary = "Delete a draw")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping(value = "/export.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportAllCsv() {
        List<DrawResponse> all = service.list();

        StringBuilder sb = new StringBuilder();
        sb.append("id,draw_id,draw_date,main_numbers,bonus_numbers\n");
        for (DrawResponse r : all) {
            String id = r.getId() == null ? "" : String.valueOf(r.getId());
            String drawIdTxt = "=\"" + r.getDrawId() + "\"";
            String dateTxt = "=\"" + r.getDrawDate().toString() + "\"";
            sb.append(quote(id)).append(',')
                    .append(quote(drawIdTxt)).append(',')
                    .append(quote(dateTxt)).append(',')
                    .append(quote(r.getMainNumbers())).append(',')
                    .append(quote(r.getBonusNumbers()))
                    .append('\n');
        }

        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] data = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] out = new byte[bom.length + data.length];
        System.arraycopy(bom, 0, out, 0, bom.length);
        System.arraycopy(data, 0, out, bom.length, data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"draws.csv\"")
                .contentType(org.springframework.http.MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(out);
    }

    private static String quote(String v) {
        if (v == null) return "\"\"";
        return "\"" + v.replace("\"", "\"\"") + "\"";
    }

}
