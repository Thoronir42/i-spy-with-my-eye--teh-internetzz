package cz.zcu.sdutends.kiwi;

import java.util.Random;

public class CrawlJobSettings extends IrJobSettings{
    // todo: move random to polite interval provider?
    private Random random;

    private int politeIntervalMin, politeIntervalMax;

    private DataSource linksSource = DataSource.Fetch;
    private String linksDataFile;

    public CrawlJobSettings() {
        this.random = new Random();
        this.setPoliteInterval(800, 1200);
    }



    public DataSource getLinksSource() {
        return linksSource;
    }
    public void setLinksSource(DataSource linksSource) {
        this.linksSource = linksSource;
    }

    public String getLinksDataFile() {
        return linksDataFile;
    }
    public void setLinksDataFile(String linksDataFile) {
        this.linksDataFile = linksDataFile;
    }

    public int getPoliteInterval() {
        return politeIntervalMin + random.nextInt(politeIntervalMax - politeIntervalMin);
    }

    public CrawlJobSettings setPoliteInterval(int val) {
        this.politeIntervalMin = this.politeIntervalMax = val;
        return this;
    }

    public CrawlJobSettings setPoliteInterval(int min, int max) {
        this.politeIntervalMin = min;
        this.politeIntervalMax = max;

        return this;
    }

    public enum DataSource {
        Fetch, Load
    }
}
