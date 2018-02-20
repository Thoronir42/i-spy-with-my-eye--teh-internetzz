package cz.zcu.kiv.nlp.cdNemovitosti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Record implements Serializable {
    private String school;
    private String typStudia;
    private String studijniObor;
    private String studijniProgram;
    private String zamereni;
    private String url;
    private List<String> texts = new ArrayList<>();

    public Record() {
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTypStudia() {
        return typStudia;
    }

    public void setTypStudia(String typStudia) {
        this.typStudia = typStudia;
    }

    public String getStudijniObor() {
        return studijniObor;
    }

    public void setStudijniObor(String studijniObor) {
        this.studijniObor = studijniObor;
    }

    public String getStudijniProgram() {
        return studijniProgram;
    }

    public void setStudijniProgram(String studijniProgram) {
        this.studijniProgram = studijniProgram;
    }

    public String getZamereni() {
        return zamereni;
    }

    public void setZamereni(String zamereni) {
        this.zamereni = zamereni;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
}
