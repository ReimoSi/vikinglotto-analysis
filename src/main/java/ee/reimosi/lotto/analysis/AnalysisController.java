package ee.reimosi.lotto.analysis;

import ee.reimosi.lotto.analysis.dto.AnalysisSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(name = "analysis", description = "Basic stats (frequencies, chi-square)")
public class AnalysisController {
    private final AnalysisService service;

    @Operation(summary = "Analysis summary (frequencies + chi-square statistic)")
    @GetMapping("/summary")
    public AnalysisSummary summary() {
        return service.summary();
    }
}
