package cz.zcu.sdutends.kiwi.utils;

public class TimeMeasure {
    private long start;

    public TimeMeasure() {
        this.start = System.currentTimeMillis();
    }

    public long duration() {
        return System.currentTimeMillis() - start;
    }

    public void reset() {
        this.start = System.currentTimeMillis();
    }
}
