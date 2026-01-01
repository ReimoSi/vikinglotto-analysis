package ee.reimosi.lotto.generate;

import ee.reimosi.lotto.generate.dto.GenerateResponse;
import ee.reimosi.lotto.generate.dto.GeneratedTicket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
@Tag(name = "generator", description = "Vikinglotto ticket generator")
public class GenerateController {

    private final NumberGeneratorService service;

    @Operation(
            summary = "Generate Vikinglotto tickets",
            description = """
            Returns N tickets with 6 unique main numbers (1..48) and 1 bonus (1..5).
            Methods:
            - `uniform` – pure uniform random
            - `anti-popular` – avoids common human patterns (calendar-heavy, arithmetic sequences, same last digits)
            """,
            tags = {"generator"}
    )
    @PostMapping
    public GenerateResponse generate(
            @Parameter(
                    name = "method",
                    description = "Generation strategy",
                    in = ParameterIn.QUERY,
                    schema = @Schema(allowableValues = {"uniform", "anti-popular"}, defaultValue = "uniform")
            )
            @RequestParam(defaultValue = "uniform") String method,

            @Parameter(
                    name = "rows",
                    description = "How many tickets to generate (1..200)",
                    in = ParameterIn.QUERY,
                    example = "5"
            )
            @RequestParam(defaultValue = "1") int rows,

            @Parameter(
                    name = "seed",
                    description = "Optional seed for reproducible results",
                    in = ParameterIn.QUERY,
                    examples = {
                            @ExampleObject(name = "no-seed", value = "null"),
                            @ExampleObject(name = "fixed-seed", value = "42")
                    }
            )
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
