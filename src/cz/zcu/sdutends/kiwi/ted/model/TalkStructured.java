package cz.zcu.sdutends.kiwi.ted.model;

import cz.zcu.sdutends.kiwi.IEntity;

import java.time.LocalDate;

public class TalkStructured implements IEntity{
    private String url;

    private String title;
    private String talker;
    private LocalDate dateRecorded;

    private String introduction;
    private TranscriptBlock[] transcript;

    public String getUrl() {
        return url;
    }

    public TalkStructured setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public TalkStructured setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTalker() {
        return talker;
    }

    public TalkStructured setTalker(String talker) {
        this.talker = talker;
        return this;
    }

    public LocalDate getDateRecorded() {
        return dateRecorded;
    }

    public TalkStructured setDateRecorded(LocalDate dateRecorded) {
        this.dateRecorded = dateRecorded;
        return this;
    }

    public String getIntroduction() {
        return introduction;
    }

    public TalkStructured setIntroduction(String introduction) {
        this.introduction = introduction;
        return this;
    }

    public TranscriptBlock[] getTranscript() {
        return transcript;
    }

    public TalkStructured setTranscript(TranscriptBlock[] transcript) {
        this.transcript = transcript;
        return this;
    }
}
