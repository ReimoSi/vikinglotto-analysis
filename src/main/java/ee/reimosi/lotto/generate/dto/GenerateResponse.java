package ee.reimosi.lotto.generate.dto;

import java.util.List;

public class GenerateResponse {
    private String method;
    private int rows;
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

