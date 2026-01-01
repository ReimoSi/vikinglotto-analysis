package ee.reimosi.lotto.draw;

import ee.reimosi.lotto.draw.dto.DrawRequest;
import ee.reimosi.lotto.draw.dto.DrawResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public List<DrawResponse> list() { return service.list(); }

    @Operation(summary = "Get a draw by ID")
    @GetMapping("/{id}")
    public DrawResponse get(@PathVariable Long id) { return service.get(id); }

    @Operation(summary = "Create a new draw")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DrawResponse create(@Valid @RequestBody DrawRequest req) { return service.create(req); }

    @Operation(summary = "Update an existing draw")
    @PutMapping("/{id}")
    public DrawResponse update(@PathVariable Long id, @Valid @RequestBody DrawRequest req) {
        return service.update(id, req);
    }

    @Operation(summary = "Delete a draw")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
