package cz.zcu.sdutends.kiwi.ted.model;

public class TranscriptBlock {
    private int time;
    private String text;

    public TranscriptBlock() {
    }

    public TranscriptBlock(int time, String text) {
        this.time = time;
        this.text = text;
    }

    public int getTime() {
        return time;
    }

    public TranscriptBlock setTime(int time) {
        this.time = time;
        return this;
    }

    public String getText() {
        return text;
    }

    public TranscriptBlock setText(String text) {
        this.text = text;
        return this;
    }
}
