package cz.zcu.sdutends.kiwi.ted.model;

import cz.zcu.sdutends.kiwi.IEntity;


public class Talk implements IEntity {
    private String url;

    private String title;
    private String talker;
    private String dateRecorded;

    private String introduction;
    private String transcript;

    public String getUrl() {
        return url;
    }

    public Talk setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Talk setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTalker() {
        return talker;
    }

    public Talk setTalker(String talker) {
        this.talker = talker;
        return this;
    }

    public String getDateRecorded() {
        return dateRecorded;
    }

    public Talk setDateRecorded(String dateRecorded) {
        this.dateRecorded = dateRecorded;
        return this;
    }

    public String getIntroduction() {
        return introduction;
    }

    public Talk setIntroduction(String introduction) {
        this.introduction = introduction;
        return this;
    }

    public String getTranscript() {
        return transcript;
    }

    public Talk setTranscript(String transcript) {
        this.transcript = transcript;
        return this;
    }
}
