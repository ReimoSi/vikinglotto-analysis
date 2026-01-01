package ee.reimosi.lotto.importing.dto;

public class CsvImportResult {
    private int imported;
    private int skipped;
    private int errors;

    public CsvImportResult() {}
    public CsvImportResult(int imported, int skipped, int errors) {
        this.imported = imported; this.skipped = skipped; this.errors = errors;
    }
    public int getImported() { return imported; }
    public void setImported(int imported) { this.imported = imported; }
    public int getSkipped() { return skipped; }
    public void setSkipped(int skipped) { this.skipped = skipped; }
    public int getErrors() { return errors; }
    public void setErrors(int errors) { this.errors = errors; }
}
