package ee.reimosi.lotto.analysis;

import ee.reimosi.lotto.analysis.dto.AnalysisSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService service;

    @GetMapping("/summary")
    public AnalysisSummary summary() {
        return service.summary();
    }
}
