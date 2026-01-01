package ee.reimosi.lotto.analysis.dto;

import java.util.List;

public class AnalysisSummary {
    public record Count(int number, int count) {}

    private int drawCount;
    private int mainPoolSize;     // nt 48
    private int mainPerDraw;      // nt 6
    private long totalMainPicks;  // drawCount * mainPerDraw

    private double chiSquareStat; // ainult statistika (ilma p-value)
    private int chiSquareDf;      // degrees of freedom (poolSize - 1)

    private List<Count> mainFrequencies;
    private List<Count> bonusFrequencies;

    public int getDrawCount() { return drawCount; }
    public void setDrawCount(int drawCount) { this.drawCount = drawCount; }
    public int getMainPoolSize() { return mainPoolSize; }
    public void setMainPoolSize(int mainPoolSize) { this.mainPoolSize = mainPoolSize; }
    public int getMainPerDraw() { return mainPerDraw; }
    public void setMainPerDraw(int mainPerDraw) { this.mainPerDraw = mainPerDraw; }
    public long getTotalMainPicks() { return totalMainPicks; }
    public void setTotalMainPicks(long totalMainPicks) { this.totalMainPicks = totalMainPicks; }
    public double getChiSquareStat() { return chiSquareStat; }
    public void setChiSquareStat(double chiSquareStat) { this.chiSquareStat = chiSquareStat; }
    public int getChiSquareDf() { return chiSquareDf; }
    public void setChiSquareDf(int chiSquareDf) { this.chiSquareDf = chiSquareDf; }
    public List<Count> getMainFrequencies() { return mainFrequencies; }
    public void setMainFrequencies(List<Count> mainFrequencies) { this.mainFrequencies = mainFrequencies; }
    public List<Count> getBonusFrequencies() { return bonusFrequencies; }
    public void setBonusFrequencies(List<Count> bonusFrequencies) { this.bonusFrequencies = bonusFrequencies; }
}

