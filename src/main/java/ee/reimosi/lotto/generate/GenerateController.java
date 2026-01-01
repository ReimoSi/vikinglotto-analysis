package ee.reimosi.lotto.generate;

import ee.reimosi.lotto.generate.dto.GenerateResponse;
import ee.reimosi.lotto.generate.dto.GeneratedTicket;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
public class GenerateController {

    private final NumberGeneratorService service;

    @PostMapping
    public GenerateResponse generate(
            @RequestParam(defaultValue = "uniform") String method,
            @RequestParam(defaultValue = "1") int rows,
            @RequestParam(required = false) Long seed
    ) {
        if (rows < 1) rows = 1;
        if (rows > 200) rows = 200;

        NumberGeneratorService.Method m = switch (method.toLowerCase()) {
            case "anti-popular", "antipopular", "anti" -> NumberGeneratorService.Method.ANTI_POPULAR;
            default -> NumberGeneratorService.Method.UNIFORM;
        };

        List<GeneratedTicket> tickets = service.generate(m, rows, seed);
        return new GenerateResponse(m.name().toLowerCase(), rows, tickets);
    }
}

