package ee.reimosi.lotto.generate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Generator response")
public class GenerateResponse {
    @Schema(description = "Selected method", example = "uniform")
    private String method;

    @Schema(description = "How many rows were generated", example = "5")
    private int rows;

    @Schema(description = "Generated tickets")
    private List<GeneratedTicket> tickets;

    public GenerateResponse() {}
    public GenerateResponse(String method, int rows, List<GeneratedTicket> tickets) {
        this.method = method; this.rows = rows; this.tickets = tickets;
    }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public List<GeneratedTicket> getTickets() { return tickets; }
    public void setTickets(List<GeneratedTicket> tickets) { this.tickets = tickets; }
}

